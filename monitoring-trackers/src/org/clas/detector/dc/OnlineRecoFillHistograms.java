package org.clas.detector.dc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.clas.detector.DetectorModulePane;
import org.clas.detector.IHistograms;
import org.clas.detector.IHistograms;
import org.jlab.detector.base.DetectorCollection;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.groot.base.ColorPalette;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
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

    public List<DetectorCollection<H2F>> get_Detector2DComponentsHistos() {
		return _Detector2DComponentsHistos;
	}

	public void set_Detector2DComponentsHistos(
			List<DetectorCollection<H2F>> _Detector2DComponentsHistos) {
		this._Detector2DComponentsHistos = _Detector2DComponentsHistos;
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
    private List<DetectorCollection<H2F>> _Detector2DComponentsHistos;
    
    private List<H1F> _Histograms;
    private List<H1F> _trackHistograms;
    private List<H2F> _TwoDHistograms;
    private double[][] arrayTDC = new double[112][6*6*6];
    private H1F componentHistogram;
    public  HashMap<String, Integer> listOfHistos;
    public  HashMap<String, Integer> listOf2DHistos;
	
    /**
     * Create List of histogram. A detector collection contains a list of
     * identical histograms for each sector and layer The array of detector
     * components corresponds to each histogram type .. here
     * detectorComponentsHistos.get(0) is the number of hits histogram
     */
    @Override
    public void CreateHistoList() {
    	
        List<DetectorCollection<H1F>> detectorComponentsHistos = new ArrayList<DetectorCollection<H1F>>();
        List<DetectorCollection<H2F>> detector2DComponentsHistos = new ArrayList<DetectorCollection<H2F>>();
        
        //level of indiv layers
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRawHitsPerEvent", 6,6,6, false, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("WireOccupancy", 6,6,6, false, 112, 0, 112));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBHitsPerEvent", 6,6,6, false, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBHitsPerEvent", 6,6,6, false, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("T0SubTDCSpectrum", 6,6,6, false, 200, 0, 400));
        detectorComponentsHistos.add(this.CreateDetectorCollection("CorrectedTimes", 6,6,6, false, 200, 0, 400));
        detectorComponentsHistos.add(this.CreateDetectorCollection("CalcDoca", 6,6,6, false, 200, 0., 2.0));
        detectorComponentsHistos.add(this.CreateDetectorCollection("TrkDoca", 6,6,6, false, 200, 0., 2.0));
        
        detector2DComponentsHistos.add(this.Create2DDetectorCollection("time_vs_TrkDoca", 6,6,6, false, 200, 0, 200, 200, 0., 2.0));
        
         // level of superlayers
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBHitsInSLPerEvent", 6,6,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBHitsInSLPerEvent", 6,6,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBClustersPerEvent", 6,6,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBClustersPerEvent", 6,6,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("HBTrkAngle", 6,6,7, true, 360, -180, 180));
        detectorComponentsHistos.add(this.CreateDetectorCollection("TBTrkAngle", 6,6,7, true, 360, -180, 180));
        detectorComponentsHistos.add(this.CreateDetectorCollection("HBClusterSize", 6,6,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("TBClusterSize", 6,6,7, true, 20, 0, 20));
        // level of region
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBHitsInRgPerEvent", 6,3,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBHitsInRgPerEvent", 6,3,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBClustersInRgPerEvent", 6,3,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBClustersInRgPerEvent", 6,3,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecHBCrossesPerEvent", 6,3,7, true, 20, 0, 20));
        detectorComponentsHistos.add(this.CreateDetectorCollection("NbRecTBCrossesPerEvent", 6,3,7, true, 20, 0, 20));
        
        detector2DComponentsHistos.add(this.Create2DDetectorCollection("y_vs_x", 6,3,7, true, 200, -200, 200, 200, -200, 200));
        detector2DComponentsHistos.add(this.Create2DDetectorCollection("uy_vs_ux", 6,3,7, true, 200, -200, 200, 200, -200, 200));
        
        listOfHistos = new HashMap<String, Integer>(detectorComponentsHistos.size()) ;
        listOf2DHistos = new  HashMap<String, Integer>(detector2DComponentsHistos.size()) ;
        
        for(int k =0; k< detectorComponentsHistos.size(); k++)
        	listOfHistos.put(detectorComponentsHistos.get(k).getName(), k);
        	
        
        for(int k =0; k< detector2DComponentsHistos.size(); k++)
        	listOf2DHistos.put(detector2DComponentsHistos.get(k).getName(), k);
        
        this.set_DetectorComponentsHistos(detectorComponentsHistos);
        this.set_Detector2DComponentsHistos(detector2DComponentsHistos);
        
    }

    private DetectorCollection<H2F> Create2DDetectorCollection(String string, 
    		int i, int j, int k, boolean fixedCompNb, int nBins1, double min1, double max1, int nBins2, double min2, double max2) {
    	DetectorCollection<H2F> detCol = new DetectorCollection<H2F>();
    	detCol.setName(string);
    	
    	 for (int sectorIdx = 0; sectorIdx < i; sectorIdx++) {
             for (int regionIdx = 0; regionIdx < j; regionIdx++) {
            	 
            	 if(fixedCompNb) {
            		 detCol.add(sectorIdx, regionIdx , k,
              				new H2F(DetectorDescriptor.getName(string, sectorIdx, regionIdx, k), nBins1, min1, max1, nBins2, min2, max2));
            	 } else {
                 	for(int compIdx =0; compIdx < k; compIdx++) {
                 		detCol.add(sectorIdx, regionIdx , compIdx,
                 				new H2F(DetectorDescriptor.getName(string, sectorIdx, regionIdx, compIdx), nBins1, min1, max1, nBins2, min2, max2));
                 	}
            	 }
             }
    	 }
		return detCol;
	}

	private DetectorCollection<H1F> CreateDetectorCollection(String string,
			int i, int j, int k, boolean fixedCompNb, int nBins, double min, double max) {
    	
    	DetectorCollection<H1F> detCol = new DetectorCollection<H1F>();
    	detCol.setName(string);
    	
    	for (int sectorIdx = 0; sectorIdx < i; sectorIdx++) {
             for (int regionIdx = 0; regionIdx < j; regionIdx++) {
            	 
            	 if(fixedCompNb) {
            		 detCol.add(sectorIdx, regionIdx , k,
              				new H1F(DetectorDescriptor.getName(string, sectorIdx, regionIdx, k), nBins, min, max));
            	 } else {
                 	for(int compIdx =0; compIdx < k; compIdx++) {
                 		detCol.add(sectorIdx, regionIdx , compIdx,
                 				new H1F(DetectorDescriptor.getName(string, sectorIdx, regionIdx, compIdx), nBins, min, max));
                 	}
            	 }
             }
    	 }
		return detCol;
	}

	public void CreateHistos() {

        List<H1F> summaryHistograms = new ArrayList<H1F>(8);
        summaryHistograms.add(new H1F("wire", 112, 1, 113));
        summaryHistograms.add(new H1F("TDC", 200, 0, 1000));
        summaryHistograms.add(new H1F("channel", 96, 0, 95));
        summaryHistograms.add(new H1F("ClusterSize_HBT", 15, 3, 18));
        summaryHistograms.add(new H1F("NumberOfHits_HBT", 100, 0, 100));
        summaryHistograms.add(new H1F("NumberOfClusters_HBT", 60, 0, 60));
        summaryHistograms.add(new H1F("NumberOfCrosses_HBT", 20, 0, 20));
        summaryHistograms.add(new H1F("ClusterSize_TBT", 15, 3, 18));
        summaryHistograms.add(new H1F("NumberOfHits_TBT", 100, 0, 100));
        summaryHistograms.add(new H1F("NumberOfClusters_TBT", 60, 0, 60));
        summaryHistograms.add(new H1F("NumberOfCrosses_TBT", 20, 0, 20));
        for (int i = 0; i < summaryHistograms.size(); ++i) {
            summaryHistograms.get(i).setTitleY("Entries");
            if(i<=2)
            	summaryHistograms.get(i).setFillColor(8);
            if(i>2 && i<=6)
            	summaryHistograms.get(i).setFillColor(9);
            if(i>6)
            	summaryHistograms.get(i).setFillColor(10);
        }

        this.set_Histograms(summaryHistograms);

        List<H1F> trackHistograms = new ArrayList<H1F>(4);
        trackHistograms.add(new H1F("trackPhi", 180, -180, 180));
        trackHistograms.add(new H1F("trackTheta", 180, 0, 180));
        trackHistograms.add(new H1F("normalizedChi2", 100, 0, 100));
        trackHistograms.add(new H1F("numberOfTracks", 10, 0, 10));
        for (int i = 0; i < 4; ++i) {
            trackHistograms.get(i).setTitleY("Entries");
            trackHistograms.get(i).setFillColor(3);
        }

        this.set_trackHistograms(trackHistograms);

        List<H2F> mapHistograms = new ArrayList<H2F>(5);
        mapHistograms.add(new H2F("wireStatus", 112, 1, 113, 12, 1, 13));
        mapHistograms.add(new H2F("occupancy", 112, 1, 113, 12, 1, 13));
        mapHistograms.add(new H2F("pulseHeight", 112, 1, 113, 12, 1, 13));
        mapHistograms.add(new H2F("pulseWidth", 112, 1, 113, 12, 1, 13));
        mapHistograms.add(new H2F("newBadWires", 112, 1, 113, 12, 1, 13));
        for (int i = 0; i < 5; ++i) {
            mapHistograms.get(i).setTitleX("Wire");
            mapHistograms.get(i).setTitleY("Layer");
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
    	
    	if(DetectorComponentsHistos.size()==0)
    		return;
    	
        int nCanvasDivisions = DetectorComponentsHistos.size();
        
        canvas.divide(1, nCanvasDivisions);
        
        for (int i = 0; i < nCanvasDivisions; i++) {
            if (DetectorComponentsHistos.get(i).hasEntry(desc.getSector(), desc.getLayer(), desc.getComponent())) {
                H1F h1 = DetectorComponentsHistos.get(i).get(desc.getSector(), desc.getLayer(), desc.getComponent());
                this.componentHistogram = DetectorComponentsHistos.get(i).get(desc.getSector(), desc.getLayer(), desc.getComponent());
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

    public void FillHistos(EvioDataEvent event, DetectorModulePane pane, long eventNr, List<String> plotName) {
    	
    	
    	
        EmbeddedCanvas canvasMaps = pane.getCanvas("Map");
        EmbeddedCanvas canvasTrack = pane.getCanvas("Track");
        EmbeddedCanvas canvasComponent = pane.getCanvas("Layer");
        EmbeddedCanvas canvasRegion = pane.getCanvas("Region");
        EmbeddedCanvas canvasSummary = pane.getCanvas("Summary");
//        canvasMaps.setAxisTitleFontSize(16);
//        canvasMaps.setAxisFontSize(16);
//        canvasMaps.setTitleFontSize(16);
//        canvasMaps.setStatBoxFontSize(8);
        
 
		//for(RawDataEntry  entry : dcdata){  
		//for(int i =0; i<bankSize; i++) {
		//	RawDataEntry  entry = dcdata.get(i);
			
			// _Histograms.get(2).fill(entry.getChannel());
			//System.out.println(" channel "+entry.getChannel());
		//}
		
        
        if (event.hasBank("DC::dgtz") == true) {
            EvioDataBank bank = (EvioDataBank) event.getBank("DC::dgtz");
            double[][][] array = new double[6][6][6];
            double[][][] array2 = new double[6][6][6];
            double[][][] array3 = new double[6][6][6];
           
            int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int superlayer = bank.getInt("superlayer", loop);
                int layer = bank.getInt("layer", loop);
                int wire = bank.getInt("wire", loop);
                int tdc = bank.getInt("tdc", loop);
                _Histograms.get(0).fill(wire);
                _Histograms.get(1).fill(tdc);
                array[sector - 1][superlayer - 1][layer - 1]++;
                array2[sector - 1][superlayer - 1][layer - 1]+=tdc;
                array3[sector - 1][superlayer - 1][layer - 1]+=wire;
                if (wire >= 0 && DCLayerIdx(sector, superlayer, layer) > 0) {
                	//System.out.println(" Filling TDC values "+ wire+" "+tdc);
                    _TwoDHistograms.get(1).fill(wire, DCLayerIdx(sector, superlayer, layer));
                    arrayTDC[wire-1][DCLayerIdx(sector, superlayer, layer) - 1] += tdc;
                }
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                	for (int k = 0; k < 6; k++) {
                		if(array[i][j][k] > 0)  
                			_DetectorComponentsHistos.get(((int) listOfHistos.get("NbRawHitsPerEvent"))).get(i, j, k).fill(array[i][j][k]);
                		if(array2[i][j][k] > 0)  
                			_DetectorComponentsHistos.get(((int) listOfHistos.get("WireOccupancy"))).get(i, j, k).fill(array2[i][j][k]);
                		if(array2[i][j][k] > 0)  
                			_DetectorComponentsHistos.get(((int) listOfHistos.get("T0SubTDCSpectrum"))).get(i, j, k).fill(array3[i][j][k]);
                    }
                }
            }
            for (int i = 0; i < 112; i++) {
                for (int j = 0; j < 6*6*6; j++) {
                    if (arrayTDC[i][j] > 0) {
//                        _TwoDHistograms.get(2).fill(i, j, array[i][j] / (eventNr * 1.0));
                        _TwoDHistograms.get(2).fill(i, j, arrayTDC[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("HitBasedTrkg::HBHits") == true) {
            double[][][] array = new double[6][6][6];
            EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBHits");
            int rows = bank.rows();
            _Histograms.get(4).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int superlayer = bank.getInt("superlayer", loop);
                int layer = bank.getInt("layer", loop);
                array[sector - 1][superlayer - 1][layer - 1]++;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                	for (int k = 0; k < 6; k++) {
                		if (array[i][j][k] > 0) 
                			_DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBHitsPerEvent"))).get(i, j, k).fill(array[i][j][k]);
                    }
                }
            }
        }
        
        if (event.hasBank("TimeBasedTrkg::TBHits") == true) {
            double[][][] array = new double[6][6][6];
            double[][][] array2 = new double[6][6][6];
            double[][][] array3 = new double[6][6][6];
            double[][][] array4 = new double[6][6][6];
            
            EvioDataBank bank = (EvioDataBank) event.getBank("TimeBasedTrkg::TBHits");
            int rows = bank.rows();
            _Histograms.get(8).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank.getInt("sector", loop);
                int superlayer = bank.getInt("superlayer", loop);
                int layer = bank.getInt("layer", loop);
                double time = bank.getDouble("time", loop);
                double doca = bank.getDouble("doca", loop);
                double trkdoca = bank.getDouble("trkDoca", loop);
                
                array[sector - 1][superlayer - 1][layer - 1]++;
                array2[sector - 1][superlayer - 1][layer - 1]+=time;
                array3[sector - 1][superlayer - 1][layer - 1]+=doca;
                array4[sector - 1][superlayer - 1][layer - 1]+=trkdoca;
                
                _Detector2DComponentsHistos.get(((int) listOf2DHistos.get("time_vs_TrkDoca"))).get(sector - 1,superlayer - 1,layer - 1).fill(trkdoca,time);
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                	for (int k = 0; k < 6; k++) {
                		if (array[i][j][k] > 0) 
                			_DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecTBHitsPerEvent"))).get(i, j, k).fill(array[i][j][k]);
                		if (array2[i][j][k] > 0) 
                    		_DetectorComponentsHistos.get(((int) listOfHistos.get("CorrectedTimes"))).get(i, j, k).fill(array2[i][j][k]);
                		if (array3[i][j][k] > 0) 
                    		_DetectorComponentsHistos.get(((int) listOfHistos.get("CalcDoca"))).get(i, j, k).fill(array3[i][j][k]);
                		if (array4[i][j][k] > 0) 
                    		_DetectorComponentsHistos.get(((int) listOfHistos.get("TrkDoca"))).get(i, j, k).fill(array4[i][j][k]);
                    }
                }
            }
        }
        
        EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBClusters");
        List<DetectorCollection<H1F>> listHistos;
        List<String> varNames;
  //      FillComponentHistos(event, bank, listHistos, varNames);
        
        if (event.hasBank("HitBasedTrkg::HBClusters") == true) {
            double[][] array = new double[6][6];
            double[][] array2 = new double[6][6];
            double[][] array3 = new double[6][6];
            double[][] array4 = new double[6][6];
            
            EvioDataBank bank1 = (EvioDataBank) event.getBank("HitBasedTrkg::HBClusters");
            int rows = bank1.rows();
            _Histograms.get(5).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank1.getInt("sector", loop);
                int superlayer = bank1.getInt("superlayer", loop);
                int Multiplicity = bank1.getInt("size", loop);
                double s = bank1.getDouble("fitSlope", loop);
                double trkAngle = Math.toDegrees(Math.atan(s));
                
                _Histograms.get(3).fill(Multiplicity);
                
                array[sector - 1][superlayer - 1]++;
                array2[sector - 1][superlayer - 1]+=Multiplicity;
                array3[sector - 1][superlayer - 1]+=trkAngle;
                array4[sector - 1][superlayer - 1]+=rows;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBClustersPerEvent"))).get(i, j, 7).fill(array[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("HBClusterSize"))).get(i, j, 7).fill(array2[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("HBTrkAngle"))).get(i, j, 7).fill(array3[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBHitsInSLPerEvent"))).get(i, j, 7).fill(array4[i][j]);
                    }
                }
            }
        }


        if (event.hasBank("TimeBasedTrkg::TBClusters") == true) {
            double[][] array = new double[6][6];
            double[][] array2 = new double[6][6];
            double[][] array3 = new double[6][6];
            double[][] array4 = new double[6][6];
            
            EvioDataBank bank1 = (EvioDataBank) event.getBank("TimeBasedTrkg::TBClusters");
            int rows = bank1.rows();
            _Histograms.get(5).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank1.getInt("sector", loop);
                int superlayer = bank1.getInt("superlayer", loop);
                int Multiplicity = bank1.getInt("size", loop);
                double s = bank1.getDouble("fitSlope", loop);
                double trkAngle = Math.toDegrees(Math.atan(s));
                
                _Histograms.get(3).fill(Multiplicity);
                
                array[sector - 1][superlayer - 1]++;
                array2[sector - 1][superlayer - 1]+=Multiplicity;
                array3[sector - 1][superlayer - 1]+=trkAngle;
                array4[sector - 1][superlayer - 1]+=rows;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecTBClustersPerEvent"))).get(i, j, 7).fill(array[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("TBClusterSize"))).get(i, j, 7).fill(array2[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("TBTrkAngle"))).get(i, j, 7).fill(array3[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecTBHitsInSLPerEvent"))).get(i, j, 7).fill(array4[i][j]);
                    }
                }
            }
        }
        
        
       
        
        if (event.hasBank("HitBasedTrkg::HBCrosses") == true) {
        	double[][] array = new double[6][3];
            EvioDataBank bank1 = (EvioDataBank) event.getBank("HitBasedTrkg::HBCrosses");
            int rows = bank1.rows();
            _Histograms.get(6).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank1.getInt("sector", loop);
                int region = bank1.getInt("region", loop);
                array[sector - 1][region - 1]++;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 3; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBCrossesPerEvent"))).get(i, j, 7).fill(array[i][j]);
                    }
                }
            }
        }

        if (event.hasBank("TimeBasedTrkg::TBCrosses") == true) {
        	double[][] array = new double[6][3];
            EvioDataBank bank1 = (EvioDataBank) event.getBank("TimeBasedTrkg::TBCrosses");
            int rows = bank1.rows();

            _Histograms.get(10).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank1.getInt("sector", loop);
                int region = bank1.getInt("region", loop);
                array[sector - 1][region - 1]++;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 3; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecTBCrossesPerEvent"))).get(i, j, 7).fill(array[i][j]);
                    }
                }
            }
        }

 


  

        if (eventNr % 20 == 0) {
            canvasComponent.draw(componentHistogram);
            if (plotName.get(0) == "occupancy") {
                canvasMaps.draw(_TwoDHistograms.get(1));
            } else if (plotName.get(0) == "average wire pulse height in TDC counts") {
              //  canvasMaps.setLogZ(true);
                canvasMaps.draw(_TwoDHistograms.get(2));
            }
            if (plotName.get(4) == "track phi0") {
                canvasTrack.draw(_trackHistograms.get(0), "S");
            } else if (plotName.get(4) == "track theta0") {
                canvasTrack.draw(_trackHistograms.get(1), "S");
            } else if (plotName.get(4) == "track normalized chi2") {
               // canvasTrack.setLogZ(true);
                canvasTrack.draw(_trackHistograms.get(2), "S");
            } else if (plotName.get(4) == "track multiplicity") {
               // canvasTrack.setLogZ(true);
                canvasTrack.draw(_trackHistograms.get(3), "S");
            }
            if (plotName.get(3) == "wire occupancy") {
            	canvasSummary.divide(1,1);
            	canvasSummary.cd(0);
                canvasSummary.draw(_Histograms.get(0), "S");
            } else if (plotName.get(3) == "tdc") {
            	canvasSummary.divide(1,1);
            	canvasSummary.cd(0);
                canvasSummary.draw(_Histograms.get(1), "S");
            } else if (plotName.get(3) == "channel occupancy") {
            	canvasSummary.divide(1,1);
            	canvasSummary.cd(0);
                canvasSummary.draw(_Histograms.get(2), "S");
            } else if (plotName.get(3) == "hit-based statistics") {
            	canvasSummary.divide(2, 2);
            	canvasSummary.cd(0);
                canvasSummary.draw(_Histograms.get(3), "S");
                canvasSummary.cd(1);
                canvasSummary.draw(_Histograms.get(4), "S");
                canvasSummary.cd(2);
                canvasSummary.draw(_Histograms.get(5), "S");
                canvasSummary.cd(3);
                canvasSummary.draw(_Histograms.get(6), "S");
            } else if (plotName.get(3) == "time-based statistics") {
            	canvasSummary.divide(2, 2);
            	canvasSummary.cd(0);
                canvasSummary.draw(_Histograms.get(7), "S");
                canvasSummary.cd(1);
                canvasSummary.draw(_Histograms.get(8), "S");
                canvasSummary.cd(2);
                canvasSummary.draw(_Histograms.get(9), "S");
                canvasSummary.cd(3);
                canvasSummary.draw(_Histograms.get(10), "S");
            }
        }
        
        /*	 if(event.hasBank("HitBasedTrkg::Crosses")==true){
			 double[][] array = new double[24][4];
	            EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::Crosses");
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

    private void FillComponentHistos(EvioDataEvent event, EvioDataBank bank,
			List<DetectorCollection<H1F>> listHistos, List<String> varNames) {
		double[][][] arrays = new double[varNames.size()][6][6];
    	for(int i =0; i< varNames.size(); i++) {
    		arrays[i][6][6] = 0;
    	}
    	if(bank!=null && bank.rows()>0) {
    		int rows = bank.rows();
            for (int loop = 0; loop < rows; loop++) {
            	for(String s : varNames) {
            		
            	}
            }
    	}
    	if (event.hasBank("HitBasedTrkg::HBClusters") == true) {
            double[][] array = new double[6][6];
            double[][] array2 = new double[6][6];
            double[][] array3 = new double[6][6];
            double[][] array4 = new double[6][6];
            
            EvioDataBank bank1 = (EvioDataBank) event.getBank("HitBasedTrkg::HBClusters");
            int rows = bank1.rows();
            _Histograms.get(5).fill(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = bank1.getInt("sector", loop);
                int superlayer = bank1.getInt("superlayer", loop);
                int Multiplicity = bank1.getInt("size", loop);
                double s = bank1.getDouble("fitSlope", loop);
                double trkAngle = Math.toDegrees(Math.atan(s));
                
                _Histograms.get(3).fill(Multiplicity);
                
                array[sector - 1][superlayer - 1]++;
                array2[sector - 1][superlayer - 1]+=Multiplicity;
                array3[sector - 1][superlayer - 1]+=trkAngle;
                array4[sector - 1][superlayer - 1]+=rows;
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (array[i][j] > 0) {
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBClustersPerEvent"))).get(i, j, 7).fill(array[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("HBClusterSize"))).get(i, j, 7).fill(array2[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("HBTrkAngle"))).get(i, j, 7).fill(array3[i][j]);
                        _DetectorComponentsHistos.get(((int) listOfHistos.get("NbRecHBHitsInSLPerEvent"))).get(i, j, 7).fill(array4[i][j]);
                    }
                }
            }
        }


    	
		
	}

	static int Module(int layer, int sector) {
        int[] shift = {0, 0, 10, 10, 24, 24, 42, 42};
        if (layer == 0 || sector == 0) {
            return -1;
        }
        return sector + shift[layer - 1] - 1;
    }

    static int DCLayerIdx(int sector, int superlayer, int layer) {
        
        if (layer == 0 || sector == 0) {
            return -1;
        }
        return (sector-1)*36 + (superlayer-1)*6 + layer;
    }

}