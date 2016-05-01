# A class which describes a book,
class Book:
    def __init__(self, id, tit, aut, abstract):
        self.isbn = id
        self.title = tit
        self.author = aut
        self.abstract = ""

        for i in abstract:
            if ord(i)>=128:
                self.abstract+=" "
            elif i == "\"":
                self.abstract += "\\\"" # make sure that quotes in text do not mess with json format
            else:
                self.abstract+=i

    def __str__(self):
        return str(self.isbn) + ", " + self.title + " by " + self.author + "\n" + self.abstract

    """
    Prints the book into json format, so it camn be used in the IR system.
    """
    def getJson(self):
        #TODO
        return "{\"title\": \"" + self.title + "\",\n" + "\"isbn\": \"" + self.isbn + "\",\n" + "\"author\": \"" + self.author + "\",\n" + "\"text\": \"" + self.abstract + "\"}"
