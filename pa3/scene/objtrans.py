import numpy as np
from util import *
import xml.etree.cElementTree as ET
import lxml.etree
import lxml.builder

class Vector3:
    def __init__(self, tks):
        if tks is not None:
            self.x = float(tks[1])
            self.y = float(tks[2])
            self.z = float(tks[3])
        else:
            self.x = 0.
            self.y = 0.
            self.z = 0.

class Vector2:
    def __init__(self, tks):
        if tks is not None:
            self.x = float(tks[1])
            self.y = float(tks[2])
        else:
            self.x = 0.
            self.y = 0.

class Object:
    def __init__(self, filename):
        self.filename = filename
        vtx = []
        nml = []
        tex = []
        vmap = {}
        tmap = {}
        tgl = []
        with open(filename+'.obj', 'r') as fin:
            txt = fin.readline()
            while txt:
                tks = txt.split()
                if len(tks) > 0:
                    if tks[0] == 'v':
                        vtx.append(Vector3(tks))
                    elif tks[0] == 'vn':
                        nml.append(Vector3(tks))
                    elif tks[0] == 'vt':
                        tex.append(Vector2(tks))
                    elif tks[0] == 'f':
                        tid = [0, 0, 0]
                        for i in range(3):
                            vs = tks[1+i].split('/')
                            tid[i] = int(vs[0])-1
                            tmap[tid[i]] = int(vs[1])-1
                            vmap[tid[i]] = int(vs[2])-1
                        tgl.append( (tid[0], tid[1], tid[2]) )
                txt = fin.readline()
        
        self.vtx = vtx
        self.nml = nml
        self.tex = tex
        self.vmap = vmap
        self.tmap = tmap
        self.tgl = tgl
    
    def scale(self, sx,sy,sz):
        vtx = self.vtx
        for i in range(len(vtx)):
            vtx[i].x *= sx;
            vtx[i].y *= sy;
            vtx[i].z *= sz;

    def translate(self,x,y,z):
        vtx = self.vtx
        for i in range(len(self.vtx)):
            vtx[i].x+=x;
            vtx[i].y+=y;
            vtx[i].z+=z;

    def rotate(self,axis,angle):
        vtx = self.vtx
        m = rotation_matrix(axis, angle)
        for i in range(len(self.vtx)):
            v = [vtx[i].x,vtx[i].y,vtx[i].z]
            v = np.dot(m, v)
            vtx[i].x = v[0]
            vtx[i].y = v[1]
            vtx[i].z = v[2]

    def tomsh(self):
        with open(self.filename+'.msh', 'w') as fout:
            vtx = self.vtx
            nml = self.nml
            tex = self.tex
            vmap = self.vmap
            tmap = self.tmap
            tgl = self.tgl
            print >> fout, len(vtx)
            print >> fout, len(tgl)
            print >> fout, 'vertices'
            for v in vtx:
                print >> fout,  v.x
                print >> fout,  v.y
                print >> fout,  v.z
            print >> fout, 'triangles'
            for v in tgl:
                print >> fout, v[0]
                print >> fout, v[1]
                print >> fout, v[2]

            print >> fout, 'texcoords'
            for i in xrange(len(vtx)):
                print >> fout, tex[tmap[i]].x
                print >> fout, tex[tmap[i]].y

            print >> fout, 'normals'
            for i in xrange(len(vtx)):
                print >> fout, nml[vmap[i]].x
                print >> fout,-nml[vmap[i]].y
                print >> fout, nml[vmap[i]].z

if __name__ == "__main__":

    earth = Object('lamp')
    # earth.scale(0.5,0.5,0.5)
    # earth.rotate([1,0,0],-np.pi/4)
    earth.tomsh()

    sun = Object('cube2')
    # sun.scale(100,100,100)
    # sun.translate(0,0,0)
    sun.tomsh()
