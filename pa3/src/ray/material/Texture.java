package ray.material;

import ray.brdf.BRDF;
import ray.brdf.Lambertian;
import ray.math.Point2;
import ray.misc.Color;
import ray.misc.Image;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture implements Material {

    BRDF brdf = new Lambertian();
    Image image;
    public Texture() {
    }

    public void setBRDF(BRDF brdf) {
        this.brdf = brdf;
    }

    public void setData(String fileName){
        try {
            BufferedImage bi = ImageIO.read(new File(fileName));
            image = Image.createFromBufferedImage(bi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BRDF getBRDF(IntersectionRecord iRec) {
        Point2 texCoords = iRec.texCoords;
        Color color = new Color();
        image.addTextureLookup(color, texCoords.x, texCoords.y);
        Lambertian lambertian = new Lambertian(color);
        return lambertian;
    }

    public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
        outRadiance.set(0, 0, 0);
    }

    public boolean isEmitter() {
        return false;
    }

}
