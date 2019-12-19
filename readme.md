# Monte-Carlo Path Tracer

## Usage:

Use Ant to build the source.

run code:
```
java -cp carbine.jar ray.ParaRayTracer scene/cbox.xml
java -cp carbine.jar ray.ParaRayTracer scene/cbox-direct.xml
java -cp carbine.jar ray.ParaRayTracer scene/cbox-global.xml
java -cp carbine.jar ray.ParaRayTracer scene/cbox-RR.xml
java -cp carbine.jar ray.ParaRayTracer scene/three-spheres.xml
java -cp carbine.jar ray.ParaRayTracer scene/earth.xml
java -cp carbine.jar ray.ParaRayTracer scene/teapot.xml
```

<div align="center">
<img src="SPHERES.XML.jpeg" alt="drawing" width="300" />
</div>


<div align="center">
<img src="cbox-RR.xml.png" alt="drawing" width="300" />
</div>


### DirectIlluminationRenderer:

300x300 resolution using 100x100 samples:
<div align="center">
<img src="cbox-direct.xml.png" alt="drawing" width="300" />
</div>



### BruteForcePathTracer:

300x300 resolution using 100x100 samples:
<div align="center">
<img src="cbox-global.xml.png" alt="drawing" width="300" />
</div>

320x180 resolution using 50x50 samples:
<div align="center">
<img src="three-spheres.xml.png" alt="drawing" width="300" />
</div>


### BruteForceRRPathTracer:

300x300 resolution using 100x100 samples:
<div align="center">
<img src="cbox-RR.xml.png" alt="drawing" width="300" />
</div>

### Texture mapping:
300x300 resolution using 100x100 samples:
<div align="center">
<img src="earth.xml.png" alt="drawing" width="300" />
</div>

<!-- ### Creative Scene: -->
300x300 resolution using 100x100 samples:
<div align="center">
<img src="earth.xml.png" alt="drawing" width="300" />
</div>

200x200 resolution using 60x60 samples:
<div align="center">
<img src="teapot.xml.png" alt="drawing" width="300" />
</div>
<!-- Cause the light source is very small, so the image is very noisy. -->
