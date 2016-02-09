from xml.dom import minidom

xmldoc = minidom.parse('.project')
itemlist = xmldoc.getElementsByTagName('name') 

print itemlist[0].firstChild.nodeValue

# print itemlist
# print itemlist[0]

# print itemlist[0].__dict__

# print itemlist[0].ownerDocument
# print itemlist[0].nodeName
# print itemlist[0].tagName
# print itemlist[0].attributes