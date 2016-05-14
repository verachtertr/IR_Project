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
import json
import Book
import User

import time, re

baseurl = "https://www.goodreads.com"

# GLOBAL VARIABLES, so it is not necessary to pass them along everytime
users = []
books = []

# Helper function to extract text from a node, also includes text in nested elements
def extractText(node):
    parts = ([node.text] +
            list(chain(*([c.text, c.tail] for c in node.getchildren()))) +
            [node.tail])
    return ''.join(filter(None, parts))

def parseBook(url, depth, maxdepth):
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

    abstract = re.sub('[^a-zA-Z ]+', '', abstract)
    if len(abstract) < 100:
        # Too short a text -> skip book
        return None

    t = re.sub('[^a-zA-Z ]+', '', title[0].text)
    a = re.sub('[^a-zA-Z ]+', '', author[0].text)

    if len(t) < 2:
        # Title is probably useless -> skip book.
        return None
    users = tree.xpath('//a[@class="user"]')

    
    # Create book object, and push it into the books vector.
    book = Book.Book(isbnr, t.strip(),a.strip(), abstract )

    print(book)

    if not book in books:
        books.append(book)

    for i in users:
        print(i.get("href"))
        parseUser(baseurl+i.get("href"), depth+1, maxdepth) # Increase depth with one, because it id going to the next level.

    return isbnr

def parseUser(url, depth, maxdepth):
    print(depth)
    if depth > maxdepth:
        return
    print(url)
    page = requests.get(url)
    tree = html.fromstring(page.content)

    name = tree.xpath('//h1[@class="userProfileName"]/text()')
    if len(name) == 0:
        return  # User has no name, or user does not exist any more.
    print(name[0].strip())

    ratings = tree.xpath('//div[@class="leftContainer"]/div[@class="leftAlignedImage"]/a[not(@class="userPagePhoto")]')
    print(len(ratings))
    print(ratings[0].get("href"))

    link = baseurl + ratings[0].get("href") + "&per_page=infinite"
    print(link)
    user = User.User(name[0].strip())
    user = parseReviews(link, user, depth, maxdepth) # just pass depth along
    users.append(user)

"""
parses users' reviews, and stores the isbn and score in the rating map.
The user parameter is the user whose reviews these are.
"""
def parseReviews(url, user, depth, maxdepth):
    page = requests.get(url)
    tree = html.fromstring(page.content)

    # scroll down
    driver = webdriver.Firefox()
    driver.get(url)
    for i in range(1,20):      # Scroll down 100 times, this should be enough
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(1)
    html_source = driver.page_source
    data = html_source.encode('utf-8')

    items = driver.find_elements_by_xpath('//tbody[@id="booksBody"]/tr/td[@class="field title"]/div/a')
    books = []
    for i in items:
        print(i.get_attribute("href"))
        books.append(i.get_attribute("href"))

    stars =  driver.find_elements_by_xpath('//tbody[@id="booksBody"]/tr/td[@class="field rating"]/div[@class="value"]/span')
    individual_stars =  driver.find_elements_by_xpath('//tbody[@id="booksBody"]/tr/td[@class="field rating"]/div[@class="value"]/span/span')
    print(len(stars))
    print(len(individual_stars))
    scores = []


    for i in range(0,len(stars)):
        score = 0
        for j in range(0,5):
            if individual_stars[(i*5)+j].get_attribute("class") == "staticStar p10":
                score+=1
        scores.append(score)
        print(score)

    driver.quit()

    for i in range(0,len(books)):
        print(books[i])
        if books[0] == None:
            continue
        else:
            bookIsbn = parseBook(books[i], depth, maxdepth)
            print(bookIsbn)
            if scores[i] > 0 and bookIsbn != None:
                user.addRating(bookIsbn, scores[i])
    return user


def readBooks(filename):
    with open(filename, 'r') as bookFile:
        data = json.load(bookFile)
        for book in data:
            b = Book.Book(book["isbn"], book["title"], book["author"], book["text"])
            books.append(b)


if __name__ == '__main__':
    readBooks('data/books.json')
    try:
        #parseBook("https://www.goodreads.com/book/show/42615.War_of_the_Rats")
        parseUser("https://www.goodreads.com/user/show/25962177-robin",0,1)	# parse starting for Robin, will skip Robin if error occurs
        #parseUser("https://www.goodreads.com/user/show/94602-kelly",0,0)
    except:
        # An error occured, print users and books, so we have something
        print("An error occured, writing accumulated books and users to file")

    try:
        parseUser("https://www.goodreads.com/user/show/25962177-robin",0,0) # parse Robin
        parseUser("https://www.goodreads.com/user/show/23496067-elise-kuylen",0,0) # parse Elise
    except:
        print("An error occured, writing accumulated books and users to file")
    #    print(sys.exc_info()[0])
    print(len(users))
    with open('data/users.json', 'w') as userfile:
        userfile.write("[")
        for user in users:
            userJson = user.getJson()
            userfile.write(userJson)
            userfile.write(",\n")
        userfile.write("]")
    print(len(books))
    with open('data/books.json', 'w') as bookfile:
        bookfile.write("[")
        initial = True
        for book in books:
            if initial:
                bookfile.write(str(book.getJson()))
                initial = False
            else:
                bookfile.write(",\n" + str(book.getJson()))
        bookfile.write("]\n")

    #parseReviews("https://www.goodreads.com/review/list/25962177-robin?utf8=%E2%9C%93&sort=rating&view=reviews&per_page=infinite")
