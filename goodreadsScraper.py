from lxml import html
from lxml.etree import tostring
from itertools import chain
import requests
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.remote import webelement
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from selenium.common.exceptions import NoAlertPresentException
import sys

import time, re

baseurl = "https://www.goodreads.com"


# Helper function to extract text from a node, also includes text in nested elements
def extractText(node):
    parts = ([node.text] +
            list(chain(*([c.text, c.tail] for c in node.getchildren()))) +
            [node.tail])
    return ''.join(filter(None, parts))

def parseBook(url):
    page = requests.get(url)
    tree = html.fromstring(page.content)

    title = tree.xpath('//h1[@id="bookTitle"]')
    print title[0].text

    isbn =  tree.xpath('//div[text()="ISBN"]')
    isbnr = int(isbn[0].getnext().text)
    print isbnr
    #This will create a list of buyers:
    description1 = tree.xpath('//div[@id="description"]/span[1]')
    extra_values = tree.xpath('//div[@id="description"]/span[1]')
    #This will create a list of prices
    description2 = tree.xpath('//div[@id="description"]/span[2]')

    print extractText(description2[0])
    #for i in extra_values[0].getchildren():
    #    print i

    users = tree.xpath('//a[@class="user"]')

    for i in users:
        print i.get("href")

def parseUser(url):
    print url
    page = requests.get(url)
    tree = html.fromstring(page.content)

    ratings = tree.xpath('//div[@class="leftContainer"]/div[@class="leftAlignedImage"]/a[2]')

    link = baseurl + ratings[0].get("href") + "&per_page=infinite"
    print link

def parseReviews(url):
    page = requests.get(url)
    tree = html.fromstring(page.content)

    # scroll down
    driver = webdriver.Firefox()
    delay = 3
    driver.get(url)
    for i in range(1,20):      # Scroll down 100 times, this should be enough
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(2)
    html_source = driver.page_source
    data = html_source.encode('utf-8')

    items = driver.find_elements_by_xpath('//tbody[@id="booksBody"]/tr/td[@class="field title"]/div/a')
    for i in items:
        print i.get_attribute("href")
    driver.quit()

    print len(items)

if __name__ == '__main__':
    parseBook("https://www.goodreads.com/book/show/3704042-tempesta-di-spade")
    parseUser(baseurl+"/user/show/39860323-rose")
    parseReviews("https://www.goodreads.com/review/list/39860323?sort=rating&view=reviews&per_page=infinite")
