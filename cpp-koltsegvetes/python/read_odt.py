import os, sys
import zipfile
import xml.dom.minidom

class PKoltsegRecord:
    def __init__(self, l = None):
        self.p_date = [0,0]
        self.p_potpEgyenleg = [2,2]
        self.p_potpBevetel = [3,3]
        self.p_potpKivetel = [4,4]
        self.p_potpKiadas = [5,5]
        self.p_pkezEgyenleg = [6,6]
        self.p_pkezBevetel = [7,7]
        self.p_pkezKiadas = [8,8]
        self.p_alberletKoltseg = [9,9]
        self.p_alberletHonnan = [10,10]
        self.p_mastolOsszeg = [11,11]
        self.p_mastolKicsoda = [12,12]
        self.p_comment = [13,13]
        self.p_htOtp = [14,14]
        self.p_htKez = [15,15]
        self.attrs = filter(lambda x: x.startswith('p_'), self.__dict__)
        if (l): self.__call__(l)

    def __call__(self, l):
        for attr in self.attrs:
            getattr(self, attr)[1] = l[getattr(self, attr)[0]]
        return self

    def printHeader(self, fs):
        fs.write(str("|").join(
            map(lambda x: x[0],
                sorted(
                    zip(self.attrs, 
                        map(lambda x: getattr(self, x)[0], self.attrs)), key=lambda x: x[1]
                ))) + "\n")

    def save(self, fs):
        fs.write(str("|").join(
            map(lambda x: x[0],
                sorted(
                    zip(map(lambda x: str(getattr(self, x)[1]), self.attrs), 
                        map(lambda x: getattr(self, x)[0], self.attrs)), key=lambda x: x[1]
                ))) + "\n")

class OdfReader:
    Type_Date = "date"
    Type_Float = "float"    
    
    def __init__(self,filename):
        """
        Open an ODF file.
        """
        self.txtfilename = filename.replace(".ods",".txt")
        self.filename = filename
        self.m_odf = zipfile.ZipFile(filename)
        self.filelist = self.m_odf.infolist()
        
#    def showManifest(self):
#        """
#        Just tell me what files exist in the ODF file.
#        """
#        for s in self.filelist:
#            s.filename, s.file_size, s.compress_size
#            print s.orig_filename
#            
#    def getContents(self):
#        """
#        Just read the paragraphs from an XML file.
#        """
#        ostr = self.m_odf.read('content.xml')
#        doc = xml.dom.minidom.parseString(ostr)
#        paras = doc.getElementsByTagName('text:p')
#        print "I have ", len(paras), " paragraphs "
#        self.text_in_paras = []
#        for p in paras:
#            for ch in p.childNodes:
#                if ch.nodeType == ch.TEXT_NODE:
#                    self.text_in_paras.append(ch.data)
#
#    def findIt(self,name):
#        for s in self.text_in_paras:
#            if name in s:
#               print s.encode('utf-8')
              
    def getRepetition(self, cell):
        try:
            cell.rep = int(cell.attributes["table:number-columns-repeated"].value)
        except KeyError: 
            cell.rep = 1

    def getValueType(self, cell):
        try:
            return cell.attributes['office:value-type'].value
        except:
            return "NoType"                    
    
    def getCharValue(self, cell):
        cell.data = "__EMPTY__"
        try:
            for ch in cell.getElementsByTagName('text:p')[0].childNodes:
                if ch.nodeType == ch.TEXT_NODE:
                    cell.data = ch.data
                    break
        except IndexError: pass
        except AttributeError: pass

    def getDateValue(self, cell):
        try:
            cell.data = cell.attributes['office:date-value'].value
        except:
            self.getCharValue(cell)
            
    def getFloatValue(self, cell):
        try:
            cell.data = cell.attributes['office:value'].value
            cell.data = int(cell.data)
        except ValueError:
            cell.data = float(cell.data)
        except:
            self.getCharValue(cell)
            print cell.toxml()
            cell.data = int(float(cell.data.replace(",","")))
            
    def getValue(self, cell):
        if (self.getValueType(cell) == OdfReader.Type_Date): 
            self.getDateValue(cell)   
            return                 
        # --
        if (self.getValueType(cell) == OdfReader.Type_Float): 
            self.getFloatValue(cell)
            return                 
        # --
        self.getCharValue(cell)

    def printAll(self):
        txtfile = open(self.txtfilename, 'w')
        recfile = open(self.txtfilename.replace(".txt", ".ascii"), 'w')
        # --
        ostr = self.m_odf.read('content.xml')
        doc = xml.dom.minidom.parseString(ostr)
        # --
        # Megkeressuk a Koltsegvetes tablat a dokumentumbol
        for table in doc.getElementsByTagName('table:table'):
            try:
#                print table.attributes['table:name'].value
                if table.attributes['table:name'].value == "Koltsegvetes": 
                    doc = table
                    break
            except KeyError: pass
        # --
        # [SORONKENT] Vegigmegyek a Koltsegvetes table minden egyes soran
        for row in doc.getElementsByTagName('table:table-row'):
            # Lokalis valtozok
            index = 0
            l_size = 20
            l = ["" for x in range(l_size)]
            # --
            # [CELLANKENT]
            for cell in row.getElementsByTagName('table:table-cell'):
                cell.index = index
                self.getRepetition(cell) # cell.rep
                self.getValue(cell) # cell.data
                # --
                l[index:min(index+cell.rep,l_size)] = [cell.data for x in range(min(cell.rep,l_size-index))]
                index += cell.rep
            # [ENDFOR - CELLANKENT]
            # --
            # print str("|").join(map(lambda x: str(x), l))
            # --
            # Soronkent beirom a fajlba ezt a valamit
            txtfile.write(str("|").join(map(lambda x: str(x), l)) + "\n")
            rec = PKoltsegRecord(l)
            rec.save(recfile)
#            rec.printHeader(recfile)
        # [ENDFOR - SORONKENT]
        # --
        txtfile.close()
                    

if __name__ == '__main__':
    """
    Pass in the name of the incoming file and the
    phrase as command line arguments. Use sys.argv[]
    """
    filename = '/home/polpe/Dropbox/koltsegvetes.ods'
    phrase = 'lidl'
    if zipfile.is_zipfile(filename):
        myodf = OdfReader(filename)
#        myodf.showManifest()       
#        myodf.getContents()        
#        myodf.findIt(phrase)       
        myodf.printAll();
    
#    a = PKoltsegRecord()
#    a(range(30,50))
#    a.printHeader(sys.stdout)
#    a.save(sys.stdout)
    
    