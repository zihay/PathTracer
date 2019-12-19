package ray.brdf;

import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

public class Specular implements BRDF {
    @Override
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir, Color outBRDFValue) {
        double F = FrDielectric(CosTheta(reflDir),etaA,etaB);
        rand = incDir.z*incDir.z;

        if(rand >p){
//            lambertian
            outBRDFValue.set(reflectance);
            outBRDFValue.scale(1/Math.PI);
            outBRDFValue.scale(1-p);
        }
        else {

            if (rand*1/p < F) {
                incDir.set(new Vector3(-reflDir.x, -reflDir.y, reflDir.z));
                outBRDFValue.set(F);
                outBRDFValue.scale(reflectance);
                outBRDFValue.scale(1 / Math.abs(CosTheta(incDir)));
                outBRDFValue.scale(p);
            } else {
                boolean entering = CosTheta(reflDir) > 0;
                double etaI = entering ? etaA : etaB;
                double etaT = entering ? etaB : etaA;

                Vector3 n = new Vector3(0, 0, 1);
                if (n.dot(reflDir) < 0.f)
                    n.scale(-1);
                if (!Refract(reflDir, n, etaI / etaT, incDir)) {
                    outBRDFValue.set(0);
                    return;
                }

                outBRDFValue.set(1-F);
                outBRDFValue.scale(T);
                outBRDFValue.scale(1 / Math.abs(CosTheta(incDir)));
                outBRDFValue.scale(p);
            }
        }
    }

    @Override
    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 outDir, Point2 seed, Color outWeight) {

    }

    @Override
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        double F = FrDielectric(CosTheta(fixedDir),etaA,etaB);
        if(rand > p){
           return 1/Math.PI * (1-p);
        }
        else {
            if (rand * 1/p< F) {
                return F*p;
            } else {
                return (1-F)*p;
            }
        }
    }

//    public Color fresnelEvaluate(double cosThetaI) {
//        double F = FrDielectric(cosThetaI, etaI, etaT);
//        return new Color(F,F,F);
//    }

    boolean Refract(Vector3 wi, Vector3 n, double eta, Vector3 wt) {
        double cosThetaI = n.dot(wi);
        double sin2ThetaI = Math.max(0.f, 1.f - cosThetaI * cosThetaI);
        double sin2ThetaT = eta * eta * sin2ThetaI;
        if (sin2ThetaT >= 1) return false;
        double cosThetaT = Math.sqrt(1 - sin2ThetaT);
        wt.set(wi);
        wt.scale(-1);
        wt.scale(eta);
        Vector3 nn = new Vector3(n);
        nn.scale(eta*cosThetaI - cosThetaT);
        wt.add(nn);
        return true;
    }

    public double FrDielectric(double cosThetaI, double etaI, double etaT) {

        cosThetaI = clamp(cosThetaI, -1, 1);
        boolean entering = cosThetaI > 0.f;
        if(!entering) {
            double tem = etaI;
            etaI = etaT;
            etaI = tem;
            cosThetaI = Math.abs(cosThetaI);
        }

        double sinThetaI = Math.sqrt(Math.max(0.f, 1 -cosThetaI * cosThetaI));
        double sinThetaT = etaI/etaT * sinThetaI;
        if (sinThetaI >= 1)
            return 1;

        double cosThetaT = Math.sqrt(Math.max(0, 1-sinThetaT*sinThetaT));

        double Rparl = ((etaT * cosThetaI) - (etaI * cosThetaT)) /
                ((etaT * cosThetaI) + (etaI * cosThetaT));
        double Rperp = ((etaI * cosThetaI) - (etaT * cosThetaT)) /
                ((etaI * cosThetaI) + (etaT * cosThetaT));

        return (Rparl * Rparl + Rperp * Rperp) / 2;
    }

    double CosTheta( Vector3 w) { return w.z; }


    public double clamp(double val, double low, double high) {
        if (val < low) return low;
        else if (val > high) return high;
        else return val;
    }

    public void setReflectance(Color reflectance) { this.reflectance.set(reflectance); }
    public void setT(Color T) { this.T.set(T); }
    public void setEtaA(double etaA) { this.etaA = etaA; }
    public void setEtaB(double etaB) { this.etaB = etaB; }

    private double etaA;
    private double etaB;
    private Color reflectance = new Color(.5,.5,.5);
    private Color T = new Color(.5,.5,.5);
    double rand = 0;
    double p = 0.3;

}
