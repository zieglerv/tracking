package org.clas.detector.cvt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.basic.IDetectorModule;
import org.jlab.clas12.basic.IDetectorProcessor;
import org.jlab.clas12.calib.DetectorModulePane;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.clas12.detector.EventDecoder;
import org.jlab.clas12.detector.FADCMaxFinder;
import org.jlab.clasrec.main.DetectorEventProcessorPane;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.cvt.services.CVTCosmicsReconstruction;
import org.root.basic.EmbeddedCanvas;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.pad.TEmbeddedCanvas;

public class EventViewer implements IDetectorProcessor, IDetectorListener, ItemListener, IDetectorModule, ActionListener {

    // Initialize Cosmics Rec
    CVTCosmicsReconstruction reco = new CVTCosmicsReconstruction();
    EventDecoder decoder = new EventDecoder();
    Decoder deco = new Decoder();

    DetectorDrawOnlineRecoComponents displays = new DetectorDrawOnlineRecoComponents();
    org.jlab.rec.cvt.svt.Geometry svt_geo = new org.jlab.rec.cvt.svt.Geometry();
    org.jlab.rec.cvt.bmt.Geometry bmt_geo = new org.jlab.rec.cvt.bmt.Geometry();

    // Define the panel and views
    DetectorShapeView2D detectorPanel = null;
    DetectorShapeView2D detectorPanel2 = null;
    DetectorShapeView2D detectorPanel3 = null;
    DetectorShapeView2D plotsPanel = null;
    //
    DetectorModulePane detectorModulePane = null;
    JLabel regionLabel = null;
    JLabel sectorLabel = null;
    JLabel sensorLabel = null;
    JLabel groupLabel1 = null;
    JLabel groupLabel2 = null;
    JLabel groupLabel3 = null;
    JLabel groupLabel4 = null;
    JLabel groupLabel5 = null;

    JFormattedTextField nEventsField;
    JFormattedTextField nSkipEventsField;
    JFormattedTextField displayEventField;

    //
    DetectorDrawComponents detFrm = new DetectorDrawComponents();
    OnlineRecoFillHistograms histos = new OnlineRecoFillHistograms();
    DetectorEventProcessorPane evPane = new DetectorEventProcessorPane();
    TEmbeddedCanvas canvas = new TEmbeddedCanvas();
    DetectorShapeTabView view = new DetectorShapeTabView();
    JFrame frame = new JFrame();

    private List<JComboBox<String>> comboBoxes = new ArrayList<JComboBox<String>>();
//    private List<String> plotName = new ArrayList<String>();
    private long eventNr = 0;
    private List<String> plotName;
    static H2D hScaler = new H2D("scalers", "scalers", 264, 0, 264, 128, 0, 128);
    static EmbeddedCanvas scalersPane = new EmbeddedCanvas();


    public EventViewer() {
        // configure reconstruction
        decoder.addFitter(DetectorType.BMT,
                new FADCMaxFinder());
        reco.init();
        ServiceConfiguration config = new ServiceConfiguration();
        config.addItem("DAQ", "data", "true");
        config.addItem("SVT", "newGeometry", "true");
        reco.configure(config);

        //
        detectorModulePane = new DetectorModulePane(4000, 2000, 1);
        detectorModulePane.addCanvas("Map");
        detectorModulePane.addCanvas("Track");
        detectorModulePane.addCanvas("Summary");
        detectorModulePane.addCanvas("Group");
        detectorModulePane.getCanvas("Group").setSize(2000, 2000);
        detectorModulePane.addCanvas("Region");
        detectorModulePane.addCanvas("Sensor");
        detectorModulePane.addCanvas("Sector");
        detectorModulePane.addCanvas("Scalers");

        // nEventsLabel = new JLabel(nEventsString);
        // nSkipEventsLabel = new JLabel(nSkipEventsString);
        // displayEventLabel = new JLabel(displayEventString);
        regionLabel = new JLabel("Region");
        sectorLabel = new JLabel("Sector");
        sensorLabel = new JLabel("Sensor");
        groupLabel1 = new JLabel("Tracker Maps");
        groupLabel2 = new JLabel("Sensor");
        groupLabel3 = new JLabel("Statistics");
        groupLabel4 = new JLabel("Summary");
        groupLabel5 = new JLabel("Track Properties");

        //
        this.detectorModulePane.getControlPanel().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        
        // configure views	   
        this.plotsPanel = new DetectorShapeView2D("Histograms");
        this.plotsPanel.setLayout(new BorderLayout());
        this.plotsPanel.add(this.canvas);
        histos.CreateHistos();

        this.detectorPanel = new DetectorShapeView2D("CVT Views");
        detFrm.CreateViews(this);
        // histos.CreateDetectorShapes(new ArrayList<DetectorCollection<H1D>>());
        this.detectorPanel.setLayout(new BorderLayout());

        this.detectorPanel2 = new DetectorShapeView2D("SVT Modules");
        this.detectorPanel2.setLayout(new BorderLayout());

        this.detectorPanel3 = new DetectorShapeView2D("BMT Modules");
        this.detectorPanel3.setLayout(new BorderLayout());

        this.evPane.addProcessor(this);
        JPanel topView = new JPanel();
        topView.setLayout(new FlowLayout());

        JPanel ModView = new JPanel();
        ModView.setLayout(new FlowLayout());
        JPanel ModView2 = new JPanel();
        ModView2.setLayout(new FlowLayout());

        topView.add(detFrm.get_TabViews().get(0));	 		 	// transverse view
        topView.add(detFrm.get_TabViews().get(1)); 	 	 		// longitudinal view
        ModView.add(detFrm.get_TabViews().get(2)); 		 		// SVT modules
        ModView2.add(detFrm.get_TabViews().get(3)); 		 	// BMT modules
        // add borders

        this.detectorPanel.add(topView, BorderLayout.PAGE_START);
        this.detectorPanel2.add(ModView, BorderLayout.PAGE_START);
        this.detectorPanel3.add(ModView2, BorderLayout.PAGE_START);
        this.frame.add(this.evPane, BorderLayout.PAGE_END);

        this.detectorModulePane.getDetectorView().addDetectorLayer(detectorPanel);
        this.detectorModulePane.getDetectorView().addDetectorLayer(this.detectorPanel2);
        this.detectorModulePane.getDetectorView().addDetectorLayer(this.detectorPanel3);
        //this.view.addDetectorLayer(this.detectorPanel);

        JComboBox<String> combo = null;
        //Strip plots/ tracker maps
        this.detectorModulePane.getControlPanel().add(groupLabel1, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("channel status");
        combo.addItem("occupancy");
        combo.addItem("average strip pulse height in ADC counts");
        combo.addItem("pulse width in ADC counts");
        combo.addItem("new bad strips");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        c.gridx = 0;
        //Components plots
        this.detectorModulePane.getControlPanel().add(groupLabel2, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("adc");
        combo.addItem("bco");
        combo.addItem("occupancy");
        combo.addItem("multiplicity");
        combo.addItem("cluster charge");
        combo.addItem("strip multiplicity");
        combo.addItem("centroid residual");
        combo.addItem("local track phi");
        combo.addItem("local track theta");
        combo.addItem("local track 3D angle");
        combo.addItem("track-angle-corrected cluster charge");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        //Statistics plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel3, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("adc");
        combo.addItem("occupancy");
        combo.addItem("cluster charge");
        combo.addItem("strip multiplicity");
        combo.addItem("centroid residual");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        //Summary/combined plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel4, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("occupancy");
        combo.addItem("adc");
        combo.addItem("cluster charge");
        combo.addItem("centroid residual");
        combo.addItem("strip multiplicity");
        combo.addItem("hit multiplicity");
        combo.addItem("cluster multiplicity");
        combo.addItem("cross multiplicity");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        // tracker object plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel5, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("track momentum");
        combo.addItem("track transverse momentum");
        combo.addItem("track phi0 vs track theta0");
        combo.addItem("track phi0");
        combo.addItem("track theta0");
        combo.addItem("track z0");
        combo.addItem("track d0");
        combo.addItem("track normalized chi2");
        combo.addItem("track multiplicity");
        combo.addItem("path length");
        combo.addItem("number of hits per track");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
                
        c.gridx=0;
        JButton buttonProcessData = new JButton("Scalers");
        buttonProcessData.addActionListener(this);
        this.detectorModulePane.getControlPanel().add(buttonProcessData, c);
        c.gridy++;

        this.view.add(detectorModulePane);
        //this.view.addDetectorLayer(this.detectorPanel2);
        // this.view.addDetectorLayer(this.detectorPanel3);
        view.addDetectorListener(this);

        this.plotName = new ArrayList<String>(comboBoxes.size());

        for (int k = 0; k < comboBoxes.size(); k++) {
            this.plotName.add("");
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this.view);
        frame.pack();
        frame.setVisible(true);
        scalersPane = detectorModulePane.getCanvas("Scalers");
        scalersPane.setLogZ(true);
        hScaler.setXTitle("Chip");
        hScaler.setYTitle("Channel");
    }

    @Override
    public void detectorSelected(DetectorDescriptor arg0) {

        if (this.plotName.get(1) == "adc") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(3));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "bco") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(4));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "occupancy") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(11));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "multiplicity") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(0));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(1));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "cluster charge") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(5));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "strip multiplicity") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(6));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "centroid residual") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(7));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "local track phi") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(8));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "local track theta") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(9));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "local track 3D angle") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(10));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Sensor"), DetectorComponentsHistos_LayerTab);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        for (int i = 0; i < comboBoxes.size(); i++) {
            if (event.getSource() == comboBoxes.get(i)
                    && event.getStateChange() == ItemEvent.SELECTED) {
                this.plotName.add(i, comboBoxes.get(i).getSelectedItem().toString());
                System.out.println(" Selected " + comboBoxes.get(i).getSelectedItem().toString());
            }
        }
    }

    DetectorCollection<Integer> SVTHits = new DetectorCollection<Integer>();
    DetectorCollection<Integer> BMTHits = new DetectorCollection<Integer>();
    DetectorCollection<Integer> SVTStrips = new DetectorCollection<Integer>();
    DetectorCollection<Integer> BMTStrips = new DetectorCollection<Integer>();

    @Override
    public void update(DetectorShape2D shape) {
        if (shape.getDescriptor().getType() == DetectorType.BST) {
            if (this.SVTHits.hasEntry(shape.getDescriptor().getSector(),
                    shape.getDescriptor().getLayer(), shape.getDescriptor().getComponent()) == true) {
                shape.setColor(255, 180, 180);
            } else if (shape.getDescriptor().getLayer() % 2 == 0) {
                shape.setColor(180, 180, 255);
            } else {
                shape.setColor(180, 255, 255);
            }
        }
        if (shape.getDescriptor().getType() == DetectorType.BMT) {
            if (this.BMTHits.hasEntry(shape.getDescriptor().getSector(),
                    shape.getDescriptor().getLayer(), shape.getDescriptor().getComponent()) == true) {
                shape.setColor(255, 180, 180);
            } else if (shape.getDescriptor().getLayer() % 2 == 0) {
                shape.setColor(255, 180, 255);
            } else {
                shape.setColor(200, 140, 200);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Scalers") == 0) {
            System.out.println("Processing scalers files... ");
                    for (int i = 31; i < 51; ++i) {
            plotScalers(i);
            System.out.println(i);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(EventViewer.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        }
    }

    public void plotScalers(int fileNr) {
        File dir = new File("/Users/ziegler/scalers");
        try {
            File fin = new File(dir.getCanonicalPath() + File.separator + "gotratest" + fileNr + ".txt");
            readScalersFile(fin);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void readScalersFile(File fin) throws IOException {
        int crate, slot, idx = 0, k = 0, strip = -1, chip = 0;
        long scaler;
        double factor = 0.968E-3; // 125000000/129156897, to convert hits to frequency in kHz
        String crateName;
//        H2D hScaler = new H2D("scalers", "scalers", 264, 0, 264, 128, 0, 128);

        BufferedReader br = new BufferedReader(new FileReader(fin));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] words = line.split(" ");
            crateName = words[0];
            slot = Integer.parseInt(words[1]);
            idx = Integer.parseInt(words[2]);
            scaler = Integer.parseInt(words[3]);
            if (idx == 2 || idx == 137 || idx == 272 || idx == 407 || idx == 542 || idx == 677 || idx == 812 || idx == 947) {
                strip = -1;
            }
            strip++;
//            System.out.println(crateName+" "+slot+" "+chip+" "+strip+" "+idx+" "+scaler);
            // if(strip>=0 && strip<128 && chip%2!=0) {
            if (strip >= 0 && strip < 128) {
                hScaler.fill(chip, strip, scaler * factor);
//                hScaler.fill(chip, strip, scaler);

//                if (scaler > 100) {
//                    System.out.println(crateName + " " + slot + " " + chip + " " + strip + " " + idx + " " + scaler);
//                }
            }
            if (strip == 127) {
                chip++;
            }
            k++;
        }

        br.close();
//        scalersPane.setLogZ(true);
        scalersPane.draw(hScaler);
    }

    @Override
    public String getAuthor() {
        // TODO Auto-generated method stub
        return "ziegler";
    }

    @Override
    public String getDescription() {
        return "Central Tracker Monitoring";
    }

    @Override
    public JPanel getDetectorPanel() {
        return this.detectorPanel;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "ziegler";
    }

    @Override
    public DetectorType getType() {
        // TODO Auto-generated method stub
        return DetectorType.SVT;
    }

    @Override
    public void processEvent(DataEvent de
    ) {
        this.eventNr++;
        EvioDataEvent event = (EvioDataEvent) de;
        EvioDataEvent decodedEvent = deco.DecodeEvent(event, decoder);
        //decodedEvent.show();

        reco.processEvent(decodedEvent);
        displays.PlotCrosses(decodedEvent, detFrm.get_ShapeViews().get(0), detFrm.get_ShapeViews().get(1), detFrm.get_TabViews().get(0), detFrm.get_TabViews().get(1), svt_geo);
        displays.PlotSVTStrips(decodedEvent, SVTHits, SVTStrips, detFrm.get_ShapeViews().get(2), svt_geo);
        displays.PlotBMTStrips(decodedEvent, BMTHits, BMTStrips, detFrm.get_ShapeViews().get(3), bmt_geo);
        histos.FillHistos(decodedEvent, this.detectorModulePane, this.eventNr, this.plotName);
    }

    public static void main(String[] args) {
        EventViewer ev = new EventViewer();

        /*
        frame.add(module.detectorPanel);
        frame.pack();
        frame.setVisible(true);
        JFrame frame2 = new JFrame();
        frame2.add(module.detectorPanel2);
        frame2.pack();
        frame2.setVisible(true);
        JFrame frame3 = new JFrame();
        frame3.add(module.detectorPanel3);
        frame3.pack();
        frame3.setVisible(true);
         */
        //EventViewer module = new EventViewer();
        //    JFrame frame = new JFrame();
        //    frame.add(module.view);
        //    frame.pack();
        //    frame.setVisible(true);
        //JFrame frame4 = new JFrame();
        //frame4.add(module.plotsPanel);
        //frame4.pack();
        //frame4.setVisible(true);
    }

}