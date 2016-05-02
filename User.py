class User:
    def __init__(self, name):
        self.name = name
        self.rated = {} # Rating vector, contains the isbn of the read books mapped to a score, 1 to 5

    def addRating(self, isbn, score):
        self.rated[isbn] = score

    def __str__(self):
        retStr = str(self.name) + "\n"
        for book, score in self.rated.items():
            retStr += "\t " + str(book) + " " + score + "\n"
        return retStr

    def getJson(self):
        retString = "{\"name\": \"" + str(self.name) + "\",\n" + "\"ratings\": "
        # Print the array of mappings.
        retString += "["
        for isbn, score in self.rated.items():
            retString += "{"
            retString += "\"book_isbn\": \"" + isbn + "\",\n"
            retString += "\"score\": " + str(score) + ",\n"
            retString += "},\n"
        retString += "]\n"
        retString += "}"
        return retString