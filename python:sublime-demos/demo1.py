
def main():
    a = ""
    methods = [method for method in dir(a) if callable(getattr(a, method))]
    for method in methods:
        print(str(method))

    # Open a file in write mode
    fo = open("foo.txt", "w")
    print "Name of the file: ", fo.name

    # Assuming file has following 5 lines
    # This is 1st line
    # This is 2nd line
    # This is 3rd line
    # This is 4th line
    # This is 5th line

    text = "This is 6th line"
    # Write a line at the end of the file.
    line = fo.write( text )

    fo.close()
    
    fo = open("foo.txt", "r")

    # Now read complete file from beginning.
    # fo.seek(0,0)
    # for index in range(6):
    #    line = fo.next()
    #    print "Line No %d - %s" % (index, line)

    # Close opend file
    fo.close()

if __name__ == '__main__':
    main()