#! /usr/bin/env python

# load a .obj file and transform it into a .msh file
#
# NOTE: for now, UV coordinates will be ignored
# 
# Changxi Zheng (Apr 8, 2013)
#

import sys, os

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

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print 'Usage:', sys.argv[0], ' [input .obj file] [output .mesh file]'
        sys.exit(-1)

    vtx = []
    nml = []
    tex = []
    vmap = {}
    tmap = {}
    tgl = []
    with open(sys.argv[1], 'r') as fin:
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

    with open(sys.argv[2], 'w') as fout:
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

