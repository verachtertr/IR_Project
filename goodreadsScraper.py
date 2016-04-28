# coding: utf-8
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

import Book
import User

import time, re

baseurl = "https://www.goodreads.com"

users = []
books = []

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
    print(title[0].text)

    author = tree.xpath('//div[@id="bookAuthors"]/span[@itemprop="author"]/a[@class="authorName"]/span')
    print(author[0].text)

    isbn =  tree.xpath('//div[text()="ISBN"]')
    isbnr = None
    if (len(isbn) > 0):
        isbnr = isbn[0].getnext().text
        isbnr = isbnr.strip()
    else:
        return None    # No ISBN -> skip book
    print(isbnr)

    abstract = ""
    description1 = tree.xpath('//div[@id="description"]/span[1]')
    description2 = tree.xpath('//div[@id="description"]/span[2]')
    if len(description1) == 0:
        return None # -> No text data -> skip book
    if len(description2) == 0:
        abstract = extractText(description1[0])
    else:
        abstract = extractText(description2[0])
    abstract = abstract.strip()
    print(abstract)

    users = tree.xpath('//a[@class="user"]')

    for i in users:
        print(i.get("href"))

    # Create book object, and push it into the books vector.
    book = Book.Book(isbnr, title[0].text.strip(),author[0].text.strip(), abstract )

    print(book)

    if not book in books:
        books.append(book)

    return isbnr

def parseUser(url):
    print(url)
    page = requests.get(url)
    tree = html.fromstring(page.content)

    name = tree.xpath('//h1[@class="userProfileName"]/text()')
    print(name[0].strip())

    ratings = tree.xpath('//div[@class="leftContainer"]/div[@class="leftAlignedImage"]/a[1]')
    print(len(ratings))
    print(ratings[0].get("href"))

    link = baseurl + ratings[0].get("href") + "&per_page=infinite"
    print(link)
    user = User.User(name[0].strip)
    user = parseReviews(link, user)
    users.append(user)

"""
parses users' reviews, and stores the isbn and score in the rating map.
The user parameter is the user whose reviews these are.
"""
def parseReviews(url, user):
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
    books = []
    for i in items:
        print(i.get_attribute("href"))
        books.append(i.get_attribute("href"))

    stars =  driver.find_elements_by_xpath('//tbody[@id="booksBody"]/tr/td[@class="field rating"]/div[@class="value"]/span')
    print(len(stars))
    scores = []
    for i in stars:
        score = 0
        for j in i.find_elements_by_xpath('.//*'):
            if j.get_attribute("class") == "staticStar p10":
                score+=1
        scores.append(score)
        print(score)

    driver.quit()

    for i in range(1,len(books)):
        print(books[i])
        bookIsbn = parseBook(books[i])
        print(bookIsbn)
        if scores[i] > 0 and bookIsbn != None:
            user.addRating(bookIsbn, scores[i])
    return user

if __name__ == '__main__':
    #parseBook("https://www.goodreads.com/book/show/42615.War_of_the_Rats")
    parseUser("https://www.goodreads.com/user/show/25962177-robin")

    print(len(users))
    print(len(books))
    #parseReviews("https://www.goodreads.com/review/list/25962177-robin?utf8=%E2%9C%93&sort=rating&view=reviews&per_page=infinite")
