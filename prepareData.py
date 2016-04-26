import csv, random, math

# The book class, so that I can easily keep the data
class Book:
    def __init__(self, id, tit, aut):
        self.isbn = id
        self.title = tit
        self.author = aut

    def __str__(self):
        return str(self.isbn) + ", " + self.title + " by " + self.author

class Reader:
    def __init__(self, nr):
        self.id = nr
        self.rated = {}

    def addRating(self, book, score):
        self.rated[book] = score

    def __str__(self):
        retStr = str(self.id) + "\n"
        for book, score in self.rated.iteritems():
            retStr += "\t " + str(book) + " " + score + "\n"
        return retStr

# helper function to make enable efficient check if item with certain key is present.
def contains(list, filter):
    for x in list:
        if filter(x):
            return x
    return False



if __name__ == '__main__':
    trainingUsers = []
    allBooks = []
    trainingBooks = []
    with open('data/BX-Users.csv', 'rb') as userfile:

        import_users = csv.reader(userfile, delimiter=';', quotechar='|')
        # Take first 100 to create a small training set
        count = 1
        for tup in import_users:
            if (count > 100):
                break
            user = Reader(tup[0])
            trainingUsers.append(user)
            count += 1

    with open('data/BX-Books.csv', 'rb') as csvfile:
        import_books = csv.reader(csvfile, delimiter=';', quotechar='|')
        for tup in import_books:
            book = Book(tup[0], tup[1], tup[2])
            allBooks.append(book)

    with open('data/BX-Book-Ratings.csv', 'rb') as ratingfile:

        import_ratings = csv.reader(ratingfile, delimiter=';', quotechar='|')
        # Take first 100 to create a small training set
        for u, book_isbn, score in import_ratings:
            user = contains(trainingUsers, lambda x: x.id == u)
            if user != False:  # find if user is in training user set
                book = contains(allBooks, lambda x:x.isbn == book_isbn)
                if book != False:
                    print "adding book: " + book_isbn
                    user.addRating(book, score)

                    # Add book to trainingBooks list
                    if contains(trainingBooks, lambda x:x.isbn == book_isbn) == False:
                        trainingBooks.append(book)
                else:
                    print "Missing book: " + book_isbn

    print len(trainingUsers)
    print len(trainingBooks)
    for user in trainingUsers:
        print user
