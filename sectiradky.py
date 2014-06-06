#! /usr/bin/python

# To change this template, choose Tools | Templates
# and open the template in the editor.
import sys
import os
import os.path
import fileinput

__author__="Tomas"
__date__ ="$1.5.2011 1:55:22$"

from sectiradky import *

if __name__ == "__main__":
    scitac = Scitac()
    scitac.secti(sys.argv[1])
    scitac.vypis()

else:
    class Scitac:

        path = ""
        counter = 0
        files = 0


        def secti(self, path):
            list = os.listdir(path)
            for i in list:
                if os.path.isdir(os.path.join(path, i)):
                    #print os.path.join(path, i)
                    self.secti(os.path.join(path, i))
                else:
                    self.files += 1
                    f = open(os.path.join(path,i), 'r')
                    soubor = 0
                    for radek in f:
                        soubor +=1
                        self.counter += 1
                    f.close()
                    #nahrazovani urcitych pasazi ve vsech souborech
                    """for line in fileinput.FileInput(os.path.join(path,i),inplace=1):
                        if "Tomas" or "tomas" in line:
                            line = line.replace("Tomas Trnka","Tomas Trnka, trnkato2@fel.cvut.cz")
                            sys.stdout.write(line)
                        else:
                            sys.stdout.write(line)  """
                    print(i,soubor,self.counter)

        def vypis(self):
            print('Celkem bylo projito {0} souboru, ktere dohromady maji {1} radku kodu'.format(self.files,self.counter))


    


