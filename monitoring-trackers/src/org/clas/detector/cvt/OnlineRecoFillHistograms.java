package org.clas.detector.cvt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.clas.detector.IHistograms;
import org.jlab.detector.base.DetectorCollection;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.groot.base.ColorPalette;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.graphics.EmbeddedPad;
import org.jlab.io.evio.EvioDataBank;
import org.jlab.io.evio.EvioDataEvent;


public class OnlineRecoFillHistograms implements IHistograms {

    public OnlineRecoFillHistograms() {
        // TODO Auto-generated constructor stub
    }

    public List<DetectorCollection<H1F>> get_DetectorComponentsHistos() {
        return _DetectorComponentsHistos;
    }

    public void set_DetectorComponentsHistos(List<DetectorCollection<H1F>> _DetectorComponentsHistos) {
        this._DetectorComponentsHistos = _DetectorComponentsHistos;
    }

    public List<H1F> get_Histograms() {
        return _Histograms;
    }

    public List<H1F> get_trackHistograms() {
        return _trackHistograms;
    }

    public List<H2F> get_2DHistograms() {
        return _TwoDHistograms;
    }

    public void set_Histograms(List<H1F> _Histograms) {
        this._Histograms = _Histograms;
    }

    public void set_trackHistograms(List<H1F> _trackHistograms) {
        this._trackHistograms = _trackHistograms;
    }

    public void set_2DHistograms(List<H2F> _TwoDHistograms) {
        this._TwoDHistograms = _TwoDHistograms;
    }

    private List<DetectorCollection<H1F>> _DetectorComponentsHistos;
    private List<H1F> _Histograms;
    private List<H1F> _trackHistograms;
    private List<H2F> _TwoDHistograms;
    private double[][] arrayADC = new double[256][132];
    private H1F componentHistogram;

    /**
     * Create List of histogram. A detector collection contains a list of
     * identical histograms for each sector and layer The array of detector
     * components corresponds to each histogram type .. here
     * detectorComponentsHistos.get(0) is the number of hits histogram
     */
    @Override
    public void CreateHistoList() {
        List<DetectorCollection<H1F>> detectorComponentsHistos = new ArrayList<DetectorCollection<H1F>>();

        DetectorCollection<H1F> dC0 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC1 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC2 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC3 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC4 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC5 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC6 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC7 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC8 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC9 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC10 = new DetectorCollection<H1F>();
        DetectorCollection<H1F> dC11 = new DetectorCollection<H1F>();

        for (int regionIdx = 0; regionIdx < 4; regionIdx++) {
            for (int sectorIdx = 0; sectorIdx < org.jlab.rec.cvt.svt.Constants.NSECT[2 * regionIdx + 1]; sectorIdx++) {
                dC2.add(sectorIdx, regionIdx, 0,
                        new H1F(DetectorDescriptor.getName("NumberOfCrosses", sectorIdx, regionIdx, 0),
                                20, 0, 20)); // whole module

                for (int sly = 0; sly < 2; sly++) {
                    dC0.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("NumberOfHits", sectorIdx, 2 * regionIdx + sly, 0),
                                    100, 0, 100));
                    dC1.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("NumberOfCluster", sectorIdx, 2 * regionIdx + sly, 0),
                                    100, 0, 100));
                    dC3.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("ADC", sectorIdx, 2 * regionIdx + sly, 0),
                                    8, 0, 8));
                    dC4.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("bco", sectorIdx, 2 * regionIdx + sly, 0),
                                    256, 0, 256));
                    dC5.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("clusterCharge", sectorIdx, 2 * regionIdx + sly, 0),
                                    300, 0, 300));
                    dC6.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("stripMultiplicity", sectorIdx, 2 * regionIdx + sly, 0),
                                    30, 0, 30));
                    dC7.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("centroidResidual", sectorIdx, 2 * regionIdx + sly, 0),
                                    100, -10, 10));
                    dC8.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("localPhi", sectorIdx, 2 * regionIdx + sly, 0),
                                    180, -180, 180));
                    dC9.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("localTheta", sectorIdx, 2 * regionIdx + sly, 0),
                                    180, 0, 180));
                    dC10.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("localTrackAngle", sectorIdx, 2 * regionIdx + sly, 0),
                                    180, 0, 180));
                    dC11.add(sectorIdx, 2 * regionIdx + sly, 0,
                            new H1F(DetectorDescriptor.getName("strip", sectorIdx, 2 * regionIdx + sly, 0),
                                    256, 1, 257));

                }
            }
        }
        detectorComponentsHistos.add(dC0);
        detectorComponentsHistos.add(dC1);
        detectorComponentsHistos.add(dC2);
        detectorComponentsHistos.add(dC3);
        detectorComponentsHistos.add(dC4);
        detectorComponentsHistos.add(dC5);
        detectorComponentsHistos.add(dC6);
        detectorComponentsHistos.add(dC7);
        detectorComponentsHistos.add(dC8);
        detectorComponentsHistos.add(dC9);
        detectorComponentsHistos.add(dC10);
        detectorComponentsHistos.add(dC11);

        this.set_DetectorComponentsHistos(detectorComponentsHistos);

    }

    public void CreateHistos() {

        List<H1F> summaryHistograms = new ArrayList<H1F>(8);
        summaryHistograms.add(new H1F("strip", 256, 1, 257));
        summaryHistograms.add(new H1F("adc", 8, 0, 8));
        summaryHistograms.add(new H1F("clusterCharge", 100, 0, 300));
        summaryHistograms.add(new H1F("centroidResidual", 100, -10, 10));
        summaryHistograms.add(new H1F("stripMultiplicity", 50, 0, 50));
        summaryHistograms.add(new H1F("numberOfHits", 100, 0, 100));
        summaryHistograms.add(new H1F("numberOfClusters", 100, 0, 100));
        summaryHistograms.add(new H1F("numberOfCrosses", 20, 0, 20));
        for (int i = 0; i < 8; ++i) {
            //summaryHistograms.get(i).setYTitle("Entries");
            summaryHistograms.get(i).setFillColor(5);
        }

        this.set_Histograms(summaryHistograms);

        List<H1F> trackHistograms = new ArrayList<H1F>(4);
        trackHistograms.add(new H1F("trackPhi", 180, -180, 180));
        trackHistograms.add(new H1F("trackTheta", 180, 0, 180));
        trackHistograms.add(new H1F("normalizedChi2", 100, 0, 100));
        trackHistograms.add(new H1F("numberOfTracks", 10, 0, 10));
        for (int i = 0; i < 4; ++i) {
           // trackHistograms.get(i).setYTitle("Entries");
            trackHistograms.get(i).setFillColor(3);
        }

        this.set_trackHistograms(trackHistograms);

        List<H2F> mapHistograms = new ArrayList<H2F>(5);
        mapHistograms.add(new H2F("channelStatus", 256, 0, 256, 132, 0, 132));
        mapHistograms.add(new H2F("occupancy", 256, 0, 256, 132, 0, 132));
        mapHistograms.add(new H2F("pulseHeight", 256, 0, 256, 132, 0, 132));
        mapHistograms.add(new H2F("pulseWidth", 256, 0, 256, 132, 0, 132));
        mapHistograms.add(new H2F("newBadStrips", 256, 0, 256, 132, 0, 132));
        for (int i = 0; i < 5; ++i) {
           // mapHistograms.get(i).setXTitle("Channel");
           // mapHistograms.get(i).setYTitle("Sensor");
        }

        this.set_2DHistograms(mapHistograms);

        this.CreateHistoList();

    }

    @Override
    /**
     * When the detector is clicked, this function is called
     *
     * @param desc the descriptor
     */
    public void DetectorSelected(DetectorDescriptor desc, EmbeddedCanvas canvas, List<DetectorCollection<H1F>> DetectorComponentsHistos) {

        int nCanvasDivisions = DetectorComponentsHistos.size();
        canvas.divide(1, nCanvasDivisions);
       
        for (int i = 0; i < nCanvasDivisions; i++) {
            if (DetectorComponentsHistos.get(i).hasEntry(desc.getSector(), desc.getLayer(), 0)) {
                H1F h1 = DetectorComponentsHistos.get(i).get(desc.getSector(), desc.getLayer(), 0);
                this.componentHistogram = DetectorComponentsHistos.get(i).get(desc.getSector(), desc.getLayer(), 0);
                this.componentHistogram.setTitle(h1.getName());
                this.componentHistogram.setFillColor(4);
                h1.setTitle(h1.getName());
                h1.setFillColor(4);
                canvas.cd(i);
                canvas.draw(h1, "S");
            }
        }

    }

    @Override
    public void SetShapeColor(DetectorShape2D shape, ColorPalette palette, int nProcessed) {
        if (_DetectorComponentsHistos.get(0).hasEntry(shape.getDescriptor().getSector(), shape.getDescriptor().getLayer(), 0)) {
            int nent = _DetectorComponentsHistos.get(0).get(shape.getDescriptor().getSector(), shape.getDescriptor().getLayer(), 0).getEntries();
            Color col = palette.getColor3D(nent, nProcessed, true);
            shape.setColor(col.getRed(), col.getGreen(), col.getBlue());
        }
    }

    public void FillHistos(EvioDataEvent event, TreeMap<String,EmbeddedCanvas> pane, long eventNr, List<String> plotName) {

        EmbeddedCanvas canvasMaps = pane.get("Map");
        EmbeddedCanvas canvasTrack = pane.get("Track");
        EmbeddedCanvas canvasComponent = pane.get("Sensor");
        EmbeddedCanvas canvasSummary = pane.get("Summary");
//        canvasMaps.setAxisTitleFontSize(16);
//        canvasMaps.setAxisFontSize(16);
//        canvasMaps.setTitleFontSize(16);
//        canvasMaps.setStatBoxFontSize(8);

        if (event.hasBank("BST::dgtz") == true) {
            EvioDataBank bank = (EvioDataBank) event.getBank("BST::dgtz");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                int channel = bank.getInt("strip", loop) - 1;
                int adc = bank.getInt("ADC", loop);
                _Histograms.get(0).fill(channel+1);
                _Histograms.get(1).fill(adc);
                if (channel >= 0 && Sensor(layer, sector) > 0) {
                    _TwoDHistograms.get(1).fill(channel, Sensor(layer, sector) - 1);
                    arrayADC[channel][Sensor(layer, sector) - 1] += adc;
                }
            }
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 132; j++) {
                    if (arrayADC[i][j] > 0) {
//                        _TwoDHistograms.get(2).fill(i, j, array[i][j] / (eventNr * 1.0));
                        _TwoDHistograms.get(2).fill(i, j, arrayADC[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Hits") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Hits");
            int rows = bank.rows();
            _Histograms.get(5).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                array[sector - 1][layer - 1]++;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(0).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Clusters") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Clusters");
            int rows = bank.rows();
            _Histograms.get(6).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                array[sector - 1][layer - 1]++;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(1).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Crosses") == true) {
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Crosses");
            int rows = bank.rows();
            _Histograms.get(7).fill(rows);
        }

        if (event.hasBank("BST::dgtz") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BST::dgtz");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                int adc = bank.getInt("ADC", loop);
                array[sector - 1][layer - 1] = adc;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(3).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BST::dgtz") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BST::dgtz");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                int bco = bank.getInt("bco", loop);
                array[sector - 1][layer - 1] = bco;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(4).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Clusters") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Clusters");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                double clusterCharge = bank.getDouble("ETot", loop);
                _Histograms.get(2).fill(clusterCharge);
                array[sector - 1][layer - 1] = clusterCharge;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(5).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Clusters") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Clusters");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                int stripMultiplicity = bank.getInt("size", loop);
                _Histograms.get(4).fill(stripMultiplicity);
                array[sector - 1][layer - 1] = stripMultiplicity;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(6).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Clusters") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Clusters");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                double centroidResidual = bank.getDouble("centroidResidual", loop);
                if((centroidResidual < -1.0001 || centroidResidual > -0.9999) && centroidResidual != 0) {
                _Histograms.get(3).fill(centroidResidual);
                array[sector - 1][layer - 1] = centroidResidual;
                }
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(7).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("CVTRec::Trajectory") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("CVTRec::Trajectory");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("SectorTrackIntersPlane", loop);
                int layer = bank.getInt("LayerTrackIntersPlane", loop);
                double localPhi = bank.getDouble("PhiTrackIntersPlane", loop);
                if (sector <= 0 || layer <= 0) {
                    continue;
                }
                array[sector - 1][layer - 1] = localPhi;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(8).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("CVTRec::Trajectory") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("CVTRec::Trajectory");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("SectorTrackIntersPlane", loop);
                int layer = bank.getInt("LayerTrackIntersPlane", loop);
                double localTheta = bank.getDouble("ThetaTrackIntersPlane", loop);
                if (sector <= 0 || layer <= 0) {
                    continue;
                }
                array[sector - 1][layer - 1] = localTheta;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(9).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("CVTRec::Trajectory") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("CVTRec::Trajectory");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("SectorTrackIntersPlane", loop);
                int layer = bank.getInt("LayerTrackIntersPlane", loop);
                double localAngle = bank.getDouble("trkToMPlnAngl", loop);
                if (sector <= 0 || layer <= 0) {
                    continue;
                }
                array[sector - 1][layer - 1] = localAngle;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(10).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("BSTRec::Hits") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Hits");
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int layer = bank.getInt("layer", loop);
                int strip = bank.getInt("strip", loop);
                array[sector - 1][layer - 1] = strip;
            }
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 8; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(11).get(i, j, 0).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("CVTRec::Cosmics") == true) {
            double[][] array = new double[24][8];
            EvioDataBank bank = (EvioDataBank) event.getBank("CVTRec::Cosmics");
            int rows = bank.rows();
            _trackHistograms.get(3).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                double phi = bank.getDouble("phi", loop);
                double theta = bank.getDouble("theta", loop);
                double chi2 = bank.getDouble("chi2", loop);
                int ndf = bank.getInt("ndf", loop);
                _trackHistograms.get(0).fill(phi);
                _trackHistograms.get(1).fill(theta);
                _trackHistograms.get(2).fill(chi2 / (double) ndf);
            }
        }

        if (eventNr % 20 == 0) {
            canvasComponent.draw(componentHistogram);
            if (plotName.get(0) == "occupancy") {
                canvasMaps.draw(_TwoDHistograms.get(1));
            } else if (plotName.get(0) == "average strip pulse height in ADC counts") {
                //canvasMaps.setLogZ(true);
                canvasMaps.draw(_TwoDHistograms.get(2));
            }
            if (plotName.get(4) == "track phi0") {
                canvasTrack.draw(_trackHistograms.get(0), "S");
            } else if (plotName.get(4) == "track theta0") {
                canvasTrack.draw(_trackHistograms.get(1), "S");
            } else if (plotName.get(4) == "track normalized chi2") {
                //canvasTrack.setLogZ(true);
                canvasTrack.draw(_trackHistograms.get(2), "S");
            } else if (plotName.get(4) == "track multiplicity") {
               // canvasTrack.setLogZ(true);
                canvasTrack.draw(_trackHistograms.get(3), "S");
            }
            if (plotName.get(3) == "occupancy") {
                canvasSummary.draw(_Histograms.get(0), "S");
            } else if (plotName.get(3) == "adc") {
                canvasSummary.draw(_Histograms.get(1), "S");
            } else if (plotName.get(3) == "cluster charge") {
                canvasSummary.draw(_Histograms.get(2), "S");
            } else if (plotName.get(3) == "centroid residual") {
                canvasSummary.draw(_Histograms.get(3), "S");
            } else if (plotName.get(3) == "strip multiplicity") {
                canvasSummary.draw(_Histograms.get(4), "S");
            } else if (plotName.get(3) == "hit multiplicity") {
                canvasSummary.draw(_Histograms.get(5), "S");
            } else if (plotName.get(3) == "cluster multiplicity") {
                canvasSummary.draw(_Histograms.get(6), "S");
            } else if (plotName.get(3) == "cross multiplicity") {
                canvasSummary.draw(_Histograms.get(7), "S");
            }
        }

        /*	 if(event.hasBank("BSTRec::Crosses")==true){
			 double[][] array = new double[24][4];
	            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Crosses");
	            int rows = bank.rows();
	            for(int loop = 0; loop < rows; loop++){
	                int sector = bank.getInt("sector", loop) ;
	                int layer  = bank.getInt("region", loop) ;
	                array[sector-1][layer-1]++;
	            }
	            for(int i = 0; i<24; i++)
	            	for(int j = 0; j<4; j++)
	            		if(array[i][j]>0)
	            			_DetectorComponentsHistos.get(2).get(i,j,0).fill(array[i][j]);
		 } */
    }

    static int Module(int layer, int sector) {
        int[] shift = {0, 0, 10, 10, 24, 24, 42, 42};
        if (layer == 0 || sector == 0) {
            return -1;
        }
        return sector + shift[layer - 1] - 1;
    }

    static int Sensor(int layer, int sector) {
        int[] shift = {0, 10, 20, 34, 48, 66, 84, 108};
        if (layer == 0 || sector == 0) {
            return -1;
        }
        return sector + shift[layer - 1] - 1;
    }

	

}