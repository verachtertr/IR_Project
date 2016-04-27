from lxml import html
from lxml.etree import tostring
from itertools import chain
import requests

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



if __name__ == '__main__':
    baseurl = "https://www.goodreads.com"
    parseBook("https://www.goodreads.com/book/show/3704042-tempesta-di-spade")
