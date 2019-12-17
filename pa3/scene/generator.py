import numpy as np
from util import *
import xml.etree.cElementTree as ET
import lxml.etree
import lxml.builder

class Scene:
    def __init__(self):
        self.camera = None
        self.objects = []
    
    def setCamera(self,camera):
        self.camera = camera

    def addObject(self, object):
        self.objects.append(object)


    def toxml(self):
        root = ET.Element("scene")
        image = ET.SubElement(root, "image").text = "200 200"
        sampler = ET.SubElement(root,"sampler", type="JitteredSampler")
        numSamplesU = ET.SubElement(sampler,"numSamplesU").text = 30
        numSamplesV = ET.SubElement(sampler,"numSamplesV").text = 30

        renderer = ET.SubElement(root,'renderer',type="DirectIlluminationRenderer")

        camera = ET.SubElement(root,'camera')
        eye = ET.SubElement(camera,'eye')
        target = ET.SubElement(camera,'target')
        up = ET.SubElement(camera,'up')
        yFOV = ET.SubElement(camera,'yFOV')

        for o in self.objects:
            material = ET.SubElement(root,'material', name='Mesh', type='')
            brdf = ET.SubElement(material,'brdf',type=o.material.type)
            ET.SubElement(brdf,'reflectance',o.material.type.color)
            
            surface = ET.SubElement(root,'surface', type='Mesh')
            data = ET.SubElement(surface,'data', o.filename+'.msh')
        

        tree = ET.ElementTree(root)
        tree.write("earth.xml")


class Camera:
    def __init__(self,position, target, up):
        self.position = position
        self.target = target
        self.up = up
        self.yFOV = 40

class Material:
    def __init__(self, type = 'Homogeneous'):
        self.type = type
        self.brdf = 'Lambertian'
        self.color = [0.5,0.5,0.5]

class Object:
    def __init__(self, filename):
        self.filename = filename
        self.material = Material()
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
                        for i in xrange(3):
                            vs = tks[1+i].split('/')
                            tid[i] = int(vs[0])-1
                            tmap[tid[i]] = int(vs[1])-1
                            vmap[tid[i]] = int(vs[2])-1
                        tgl.append( (tid[0], tid[1], tid[2]) )
                txt = fin.readline()
        
        self.vtx = np.array(vtx)
        self.nml = np.array(nml)
        self.tex = tex
        self.vmap = vmap
        self.tmap = tmap
        self.tgl = tgl
    
    def setMaterial(self, material):
        self.material = material

    def scale(self, sx,sy,sz):
        vtx = self.vtx
        for i in range(len(vtx)):
            pos[i][0] *= sx;
            pos[i][1] *= sy;
            pos[i][2] *= sz;

    def translate(self,x,y,z):
        vtx = self.vtx
        for i in range(len(self.vtx)):
            vtx[i][0]+=x;
            vtx[i][1]+=y;
            vtx[i][2]+=z;

    def rotate(self,axis,angle):
        vtx = self.vtx
        m = rotation_matrix(axis, angle)
        for i in range(len(self.vtx)):
            vtx[i] = np.dot(m, vtx[i])

    def tomsh(self):
        with open(filename+'.msh', 'w') as fout:
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

    earth = Object('earth')

    sun = Ojbect('cube')
    
    position = [0,0,1000]
    target = [0,0,0]
    up = [0,1,0]

    camera = Camera(position, target, up)

    scene = Scene()
    scene.setCamera(camera)
    scene.addObject(earth)
    scene.addObject(sun)


