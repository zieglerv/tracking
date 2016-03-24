package org.jlab.rec.dc.cluster;

import static java.lang.Math.cos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.GeometryLoader;
import org.jlab.rec.dc.hit.Hit;
import org.jlab.rec.dc.hit.FittedHit;


/**
 * A hit pruning algorithm to reject noise that gives a pattern of hits that are continguous in the same layer
 * The algorithm first puts the hits in arrays according to their layer and wire number.
 * Each such array contains all the hits in the same layer.
 * The algorithm then collects groups of contiguous hits into a list of hits.
 * The n-first and n-last hits in the list are kept, and all other hits inbetween pruned.
 * The value of n depends on the size of the list.
 * A loose clustering algorithm loops over all superlayers, in a sector and finds groups of hits with
 * contiguous wire index numbers.  These clusters (called clumps of hits) are delimited by layers with
 * no hits at a particular wire coordinate.
 * These clusters are then refined using fits to their respective wire indexes as a function of layer number
 * to identify parallel tracks or overlapping track candidates.
 *
 * */

public class ClusterFinder  {

	public ClusterFinder() {

	}

	// cluster finding algorithm
	// the loop is done over sector and superlayers
	// idx        = superlayer*sector + superlayer
	// sector     = idx/nsect + 1 (starting at 1)
	// superlayer = idx%nsect + 1 (     "      )

	int nsect = Constants.NSECT;
	int nslay = Constants.NSLAY;
	int nlayr = Constants.NLAYR;
	int nwire = Constants.NWIRE;



	private Hit[][][] HitArray = new Hit[nsect*nslay][nwire][nlayr];
	/**
	 *
	 * @return gets 3-dimentional array of hits as Array[total_nb_sectors*total_nb_superlayers][total_nb_wires][total_nb_layers]
	 */
	public Hit[][][] getHitArray() {
		return HitArray;
	}

	/**
	 * Sets the hit array Array[total_nb_sectors*total_nb_superlayers][total_nb_wires][total_nb_layers]
	 * @param hitArray
	 */
	public void setHitArray(Hit[][][] hitArray) {
		HitArray = hitArray;
	}

	/**
	 * Fills 3-dimentional array of hits from input hits
	 * @param hits the unfitted hit
	 */
	public void fillHitArray(List<Hit> hits) {

		// a Hit Array is used to identify clusters

		Hit[][][] hitArray= new Hit[nsect*nslay][nwire][nlayr];

		// initializing non-zero Hit Array entries
		// with valid hits
		for(Hit hit : hits)  {
			if(passHitSelection(hit)) {
				int ssl = (hit.get_Sector()-1)*nsect + (hit.get_Superlayer() - 1);
				int wi  = hit.get_Wire() - 1;
				int la  = hit.get_Layer() - 1;

				if(wi>=0 && wi<nwire)
					hitArray[ssl][wi][la] = hit;
			}
		}
		this.setHitArray(hitArray);

	}
	/**
	 * Fills 3-dimentional array of hits from input hits
	 * @param hits the unfitted hit
	 */
	public void fillHitArray(List<Hit> hits, int rejectLayer) {

		// a Hit Array is used to identify clusters

		Hit[][][] hitArray= new Hit[nsect*nslay][nwire][nlayr];

		// initializing non-zero Hit Array entries
		// with valid hits
		for(Hit hit : hits)  {
			if(passHitSelection(hit) && hit.get_Layer()!=rejectLayer) {
				int ssl = (hit.get_Sector()-1)*nsect + (hit.get_Superlayer() - 1);
				int wi  = hit.get_Wire() - 1;
				int la  = hit.get_Layer() - 1;

				if(wi>=0 && wi<nwire)
					hitArray[ssl][wi][la] = hit;
			}
		}
		this.setHitArray(hitArray);

	}
	
	/**
	 * Prunes the input hit list to remove noise candidates;
	 * the algorithm finds contiguous hits in a layer (column) and removes hits according to the
	 * number (Nc) of such contiguous hits in a given layer.
	 * If Nc 3, 4, only the first and last hit in that column; if Nc > 4, keep the first 2 and
	 * last 2 hits in that column,  if Nc > 10 remove all hits in that column.
	 *
	 * @param hits the unfitted hits
	 */
	public void pruneHitList(List<Hit> hits) {

		for(int ssl=0; ssl<nsect*nslay; ssl++)
		{
			// for each ssl, a loop over the wires
			// is done to define clusters
			for(int la=0; la<nlayr; la++)
			{
				int wi  = 0;  // wire index in the loop

				// looping over all wires
				while(wi<nwire)
				{
					// if there's a hit, it's a potential column of hits in a layer
					if(HitArray[ssl][wi][la] != null)
					{
						// vector of hits in the cluster candidate
						List<Hit> hitsInLayer = new ArrayList<Hit>();

						// adding all hits in this and all the subsequent
						// strip until there's a strip with no hit
						while(HitArray[ssl][wi][la] != null  && wi<nwire)
						{
							hitsInLayer.add(HitArray[ssl][wi][la]);

							wi++;
						}

						Collections.sort(hitsInLayer);

						//int NbEndCells2Keep = 1;
						int NbEndCells2Keep = Constants.DEFAULTNBENDCELLSTOKEEP;
						if(hitsInLayer.size()>2)
						{
							if(hitsInLayer.size()>4)
								NbEndCells2Keep = Constants.NBENDCELLSTOKEEPMORETHAN4HITSINCOLUMN; //possible tracks crossing
							if(hitsInLayer.size()>10)
								NbEndCells2Keep = 0; //kill all hits

							List<Hit> insublist = new ArrayList<Hit>();
							for (int si=NbEndCells2Keep; si<hitsInLayer.size()-NbEndCells2Keep; si++)
							{
								insublist.add(hitsInLayer.get(si));
							}

							//remove the bad hits from hit list
							for (int si=0; si<insublist.size(); si++)
							{

								Hit hitToRmv = HitArray[ssl][insublist.get(si).get_Wire() -1][la];
								hits.remove(hitToRmv);
								HitArray[ssl][insublist.get(si).get_Wire() -1][la] = null;
							}
						}
					}
					wi++;
				}
			}
		}
	}


	/**
	 * @param allhits the list of unfitted hits
	 * @return List of clusters
	 */
	public List<Cluster> findClumps(List<Hit> allhits)  { // a clump is a cluster that is not filtered for noise
		Collections.sort(allhits);

		List<Cluster> clumps = new ArrayList<Cluster>();


		// looping over each superlayer in each sector
		// each superlayer is treated independently
		int cid = 1;  // cluster id, will increment with each new good cluster

		for(int ssl=0; ssl<nsect*nslay; ssl++)  {
			// for each ssl, a loop over the wires
			// is done to define clusters
			// clusters are delimited by layers with
			// no hits at a particular wire coordinate

			int wi  = 0;  // wire number in the loop
			// looping over all physical wires
			while(wi<nwire)  {
				// if there's a hit in at least one layer, it's a cluster candidate
				if(count_nlayers_hit(HitArray[ssl][wi])!=0)  {
					List<Hit> hits = new ArrayList<Hit>() ;

					// adding all hits in this and all the subsequent
					// wires until there's a wire with no layers hit
					while(count_nlayers_hit(HitArray[ssl][wi])>0 && wi<nwire)  {
						// looping over all physical wires

						for(int la=0; la<nlayr; la++) {

							if(HitArray[ssl][wi][la]!=null) {

								hits.add(HitArray[ssl][wi][la]);
								//System.out.println(" adding hit "+HitArray[ssl][wi][la].printInfo()+" to cid "+cid);
							}
						}
						wi++;

					}


					// Need at least MIN_NLAYERS
					if(count_nlayers_in_cluster(hits) >= Constants.DC_MIN_NLAYERS)  {

						// cluster constructor DCCluster(hit.sector,hit.superlayer, cid)
						Cluster this_cluster = new Cluster((int) (ssl/nsect) + 1, (int)(ssl%nsect) + 1, cid++);
						//System.out.println(" created cluster "+this_cluster.printInfo());
						this_cluster.addAll(hits);

						clumps.add(this_cluster);

					}
				}

				// if no hits, check for next wire coordinate

				wi++;

			}
		}
		return clumps;
	}


	/**
	 * @param allhits the list of unfitted hits
	 * @return clusters of hits. Hit-based tracking linear fits to the wires are done to determine the clusters.  The result is a fitted cluster
	 */
	public List<FittedCluster> findClusters(List<Hit> allhits) {

		ClusterFinder gcf = new ClusterFinder();

		//fill array of hit
		gcf.fillHitArray(allhits);
		
		//prune noise
		gcf.pruneHitList(allhits);
		
		//find clumps of hits
		List<Cluster> clusters = gcf.findClumps(allhits);

		// create cluster list to be fitted
		List<FittedCluster> selectedClusList =  new ArrayList<FittedCluster>();

		for(Cluster clus : clusters) {
			if(passSelector(clus)) {
				//System.out.println(" I passed this cluster "+clus.printInfo());
				FittedCluster fclus = new FittedCluster(clus);
				selectedClusList.add(fclus);
			}
		}

		// create list of fitted clusters
		List<FittedCluster> fittedClusList =  new ArrayList<FittedCluster>();
		List<FittedCluster> refittedClusList =  new ArrayList<FittedCluster>();
		for(FittedCluster clus : selectedClusList) {
            
			clus.clusterFitter("HitBased", false);
		
			if(clus.get_fitProb()>Constants.HITBASEDTRKGMINFITHI2PROB || clus.size()<Constants.HITBASEDTRKGNONSPLITTABLECLSSIZE) {
				fittedClusList.add(clus); //if the chi2 prob is good enough, then just add the cluster, or if the cluster is not split-able because it has too few hits
				//System.out.println(" I passed this cluster based on fit prob "+clus.printInfo());
			} else {
				//System.out.println(" I am trying to split this cluster  "+clus.printInfo());
				List<FittedCluster> splitClus =  gcf.splitClusters(clus, selectedClusList.size());
				fittedClusList.addAll(splitClus);
				//System.out.println(" After trying to split the cluster I get  "+splitClus.size()+" clusters : ");
				//for(FittedCluster cl : splitClus)
				//	System.out.println(cl.printInfo());
			}
		}

		
		for(FittedCluster clus : fittedClusList) {
			if(clus!=null) {
				
				clus.clusterFitter("HitBased", false);
				
				// update the hits
				for(FittedHit fhit : clus) {
					fhit.set_TrkgStatus(0);
					fhit.updateHitPosition();	
					fhit.set_AssociatedClusterID(clus.get_Id());
					
				}
				// Refit
				clus.clusterFitter("HitBased", false);
				for(FittedHit fhit : clus) {
					//double calc_doca = (fhit.get_X()-clus.get_clusterLineFitSlope()*fhit.get_Z()-clus.get_clusterLineFitIntercept());
					
					double x = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().x();
					double z = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().z();
					
					double calc_doca = (x-clus.get_clusterLineFitSlope()*z-clus.get_clusterLineFitIntercept())*cos(Math.toRadians(6.));
					
					fhit.set_ClusFitDoca(calc_doca);
				}
				refittedClusList.add(clus);
				
			}

			
		}

		
		return refittedClusList;

	}

	public List<FittedCluster> timeBasedClusters(List<FittedHit> fhits) {
		
		List<FittedCluster> clusters = new ArrayList <FittedCluster>();
		int NbClus =-1;
		for(FittedHit hit : fhits) {
			
			if(hit.get_AssociatedClusterID()==-1)
				continue;
			if(hit.get_AssociatedClusterID()>NbClus)
				NbClus = hit.get_AssociatedClusterID();
		}
		
		FittedHit[][] HitArray = new FittedHit[fhits.size()][NbClus+1];
		
		int index =0;
		for(FittedHit hit : fhits) {
			if(hit.get_AssociatedClusterID()==-1)
				continue;
			HitArray[index][hit.get_AssociatedClusterID()] = hit;
			hit.updateHitPosition();
			
			index++;
		}
		
		for(int c = 0; c<NbClus+1; c++) {
			List<FittedHit> hitlist = new ArrayList<FittedHit>();
			for(int i = 0; i<index; i++) {
				if(HitArray[i][c]!=null) {
					hitlist.add(HitArray[i][c]);					
				}
			}
			if(hitlist.size()>0) {
				
				Cluster cluster = new Cluster(hitlist.get(0).get_Sector(),hitlist.get(0).get_Superlayer(),c);
				FittedCluster fcluster = new FittedCluster(cluster);			
				fcluster.addAll(hitlist);			
				clusters.add(fcluster);
			}
		}
		
		for(FittedCluster clus : clusters) {
			if(clus!=null) {			
				// update the hits
				for(FittedHit fhit : clus) {
					
					if(fhit.get_TrkgStatus()==0) {  // setting flag to indicate hit-based fits have been done
						double cosTrkAngle = Math.cos(Math.atan(clus.get_clusterLineFitSlope()));
						fhit.updateHitPositionWithTime(cosTrkAngle); // to do --> take error on trk angle into account!!!
						
					}
				}
				// Refit
				clus.clusterFitter("TimeBased", false);		
				for(FittedHit fhit : clus) {
					
					double x = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().x();
					double z = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().z();
					
					double calc_doca = (x-clus.get_clusterLineFitSlope()*z-clus.get_clusterLineFitIntercept())*cos(Math.toRadians(6.));
					fhit.set_ClusFitDoca(calc_doca);
				}
			}
		}
		return clusters;
		
	}
	/**
	 *
	 * @param clus the cluster
	 * @return a boolean that can be used to impose an additional quality requirement on the cluster.
	 */
	private boolean passSelector(Cluster clus) {
		// TODO
		return true;
	}

	/**
	 *
	 * Pattern Recognition step for identifying clusters in a clump:
	 * Find the points that are consistent with belonging to the same cluster.
	 * This step precedes the initial estimates of the track segments which require further refining.
	 * The method employed is that of Hough Transforms.
	 * Define the dimension of the r-theta accumulator array used for pattern recognition of (rho, phi) points.
	 * @param clus the fitted cluster. This cluster is examined for overlaps and // tracks.
	 * @param nextClsStartIndex the index of the next cluster in the splitted cluster.
	 * @return a list of fitted clusters
	 */
	public List<FittedCluster> splitClusters(FittedCluster clus, int nextClsStartIndex) {

		/// The principle of Hough Transform in pattern recognition is as follows.
		/// For every point (rho, phi) on a line there exists an infinite number of
		/// lines that go through this point.  Each such line can be parametrized
		/// with parameters (r, theta) such that r = rho * cos(theta) + phi * sin(theta),
		/// where theta is the polar angle of the line which is perpendicular to it
		/// and intersects the origin, and r is the distance between that line and
		/// the origin, for a given theta.
		/// Hence a point in (rho, phi) parameter space corresponds to a sinusoidal
		/// curve in (r, theta) parameter space, which is the so-called Hough-
		/// transform space.
		/// Points that fall on a line in (rho, phi) space correspond to sinusoidal
		/// curves that intersect at a common point in Hough-transform space.
		/// Remapping this point in (rho, phi) space yields the line that contains
		/// the points in the original line.
		/// This method is a pattern recognition tool used to select groups of points
		/// belonging to the same shape, such as a line or a circle.
		/// To find the ensemble of points belonging to a line in the original space
		/// the Hough transform algorithm makes use of an array, called an accumulator.
		/// The principle of the accumulator is a counting method.
		/// The dimensions of the array is equal to the number of parameters in
		/// Hough transform space, which is 2 corresponding to the (r, theta) pair
		/// in our particular case.
		/// The bin size in the array are finite intervals in r and theta, which are
		/// called accumulator cells.
		/// The bin content of cells along the curve of discretized (r, theta) values
		/// get incremented.  The cell with the highest count corresponds to the
		/// intersection of the curves.  This is a numerical method to find
		/// the intersection of any number of curves.
		/// Once the accumulator array has been filled with all (r, theta) points
		/// peaks and their associated (rho, phi) points are determined.
		/// From these, sets of points belonging to common lines can be
		/// determined.
		/// This is a preliminary pattern recognition method used to identify
		/// reconstructed hits belonging to the same track-segment.

		int N_t = 180;

		// From this calculate the bin size in the theta accumulator array
		double ThetaMin = 0.;
		double ThetaMax = 2.*Math.PI;
		double SizeThetaBin   = (ThetaMax-ThetaMin)/((double) N_t);

		// Define the dimension of the r accumulator array

		int N_r = 130;
		// From this calculate the bin size in the theta accumulator array
		double RMin =  -130;
		double RMax =  130;

		int[][] R_Phi_Accumul;
		R_Phi_Accumul = new int[N_r][N_t];

		// cache the cos and sin theta values [for performance improvement]
		double[] cosTheta_RPhi_array;
		double[] sinTheta_RPhi_array;

		// the values corresponding to the peaks in the array
		double[] binrMaxR_Phi;
		double[] bintMaxR_Phi;
		binrMaxR_Phi = new double[N_r*N_t];
		bintMaxR_Phi = new double[N_r*N_t];

		cosTheta_RPhi_array = new double[N_t];
		sinTheta_RPhi_array = new double[N_t];

		for(int j_t=0; j_t<N_t; j_t++)  {
			// theta_j in the middle of the bin :
			double theta_j = ThetaMin + (0.5 + j_t)*SizeThetaBin;
			cosTheta_RPhi_array[j_t] = Math.cos(theta_j);
			sinTheta_RPhi_array[j_t] = Math.sin(theta_j);
		}


		// loop over points to fill the accumulator arrays
		for(int i = 0; i < clus.size(); i++) {

			double rho = clus.get(i).get_lX();
			double phi = clus.get(i).get_lY();


			// fill the accumulator arrays
			for(int j_t=0; j_t<N_t; j_t++) {
				// cashed theta_j in the middle of the bin :
				//double theta_j   = ThetaMin + (0.5 + j_t)*SizeThetaBin;
				// r_j corresponding to that theta_j:
				double r_j  = rho*cosTheta_RPhi_array[j_t] + phi*sinTheta_RPhi_array[j_t];
				// this value of r_j falls into the following bin in the r array:
				int j_r = (int) Math.floor(N_r*(r_j - RMin)/(float) (RMax - RMin));

				// increase this accumulator cell:
				R_Phi_Accumul[j_r][j_t]++;
			}

		}

		// loop over accumulator array to find peaks (allows for more than one peak for multiple tracks)
		// The accumulator cell count must be at least half the total number of hits
		// Make binrMax, bintMax arrays to allow for more than one peak

		int threshold = Constants.DC_MIN_NLAYERS;
		int nbPeaksR_Phi = 0;


		// 1st find the peaks in the R_Phi accumulator array
		for(int ibinr1=0;ibinr1<N_r;ibinr1++) {
			for(int ibint1=0;ibint1<N_t;ibint1++) {
				//find the peak

				if(R_Phi_Accumul[ibinr1][ibint1]>= Constants.DC_MIN_NLAYERS ) {

					if(R_Phi_Accumul[ibinr1][ibint1]>threshold)
						threshold = R_Phi_Accumul[ibinr1][ibint1];

					binrMaxR_Phi[nbPeaksR_Phi] = ibinr1;
					bintMaxR_Phi[nbPeaksR_Phi] = ibint1;
					nbPeaksR_Phi++;

				}
			}
		}

		// For a given Maximum value of the accumulator, find the set of points associated with it;
		//  for this, begin again loop over all the points
		List<FittedCluster> splitclusters = new ArrayList<FittedCluster>();

		for(int p = nbPeaksR_Phi-1; p>-1; p--) {
			// Make a new cluster

			FittedCluster newClus = new FittedCluster(clus.getBaseCluster());

			//remove all existing hits and add only the ones passing the criteria below
			//newClus.removeAll(clus);

			for(int i = 0; i < clus.size(); i++) {
				double rho = clus.get(i).get_X();
				double phi = clus.get(i).get_lY();

				for(int j_t=0; j_t<N_t; j_t++) {
					// theta_j in the middle of the bin :
					//double theta_j   = ThetaMin + (0.5 + j_t)*SizeThetaBin;
					// r_j corresponding to that theta_j:
					double r_j = rho*cosTheta_RPhi_array[j_t] + phi*sinTheta_RPhi_array[j_t];
					// this value of r_j falls into the following bin in the r array:
					int j_r = (int) Math.floor( N_r*(r_j - RMin)/(float) (RMax - RMin));

					// match bins:
					if(j_r == binrMaxR_Phi[p] && j_t == bintMaxR_Phi[p])
						newClus.add(clus.get(i));  // add this hit

				}
			}
			//no gaps
			List<Hit> contigArrayOfHits = new ArrayList<Hit>(); //contiguous cluster

			boolean passCluster = true;
			for (int l = 1; l <= Constants.NLAYR; l++) {
				for (int i = 0; i< newClus.size(); i++) {
					if(newClus.get(i).get_Layer()==l)
						contigArrayOfHits.add(newClus.get(i));
				}
			}
			for(int i = 0; i<contigArrayOfHits.size()-1; i++) { //if there is a gap do not include in list
				if(contigArrayOfHits.get(i+1).get_Layer()-contigArrayOfHits.get(i).get_Layer() >1)
					passCluster=false;
			}
			//require 4 layers to make a cluster
			if(count_nlayers_in_cluster(contigArrayOfHits) < Constants.DC_MIN_NLAYERS)
				passCluster=false;

			//require consistency with line
			newClus.clusterFitter("HitBased", false);
			if(newClus.get_fitProb()<0.9)
				passCluster=false;

			if(!(splitclusters.contains(newClus)) && passCluster)
				splitclusters.add(newClus);
		}

		// make new clusters
		List<FittedCluster> selectedClusList =  new ArrayList<FittedCluster>();


		int newcid = nextClsStartIndex;
		for(FittedCluster cluster : splitclusters) {
			cluster.set_Id(newcid++);
			cluster.clusterFitter("HitBased", false);

			FittedCluster bestCls = bestCluster(cluster, splitclusters);

			if(bestCls!=null) {

				if(!(selectedClusList.contains(bestCls)))
					selectedClusList.add(bestCls);
			}
		}
		
		int splitclusId =1;
		if(selectedClusList.size()!=0) {
			for(FittedCluster cl : selectedClusList) {
				cl.set_Id(clus.get_Id()*1000+splitclusId);
				splitclusId++;
			}
		}
		
		if(selectedClusList.size()==0)
			selectedClusList.add(clus); // if the splitting fails, then return the original cluster
		return selectedClusList;
	}

	public List<List<Hit>> byLayerListSorter(List<Hit> DCHits, int sector, int superlyr) {

		List<List<Hit>> hitsinlayr_array = new ArrayList<List<Hit>>();

		for(int l = 0; l<nlayr; l++) {
			List<Hit> hitsinlayr = new ArrayList<Hit>();
			for(Hit hitInList : DCHits) {
				if(hitInList!=null) {
					if(hitInList.get_Layer()==l+1 && hitInList.get_Sector()==sector && hitInList.get_Superlayer()==superlyr) {
						hitsinlayr.add(hitInList);
					}
				}
			}
			hitsinlayr_array.add(l,hitsinlayr);
		}
		return hitsinlayr_array;
	}


	/**
	 *
	 * @param hits_inlayer the hits in a given layer
	 * @return the number of layers hit at a certain wire coordinate
	 */
	public int count_nlayers_hit(Hit[] hits_inlayer)
	{

		Hit[] allhits_inlayer = new Hit[nlayr];
		allhits_inlayer = hits_inlayer;

		int nlayers_hit = 0;
		for(int la=0; la<nlayr; la++)
		{
			if(allhits_inlayer[la]!=null)
				nlayers_hit++;

		}
		return nlayers_hit;
	}

	/**
	 *
	 * @param hitsInClus the hits in a cluster
	 * @return the number of layers in a cluster
	 */
	public int count_nlayers_in_cluster(List<Hit> hitsInClus)  {
		// count hits in each layer
		int[] nlayers = new int[nlayr];
		for(int l=0; l<nlayr; l++)  {
			nlayers[l] = 0;
			for(int h=0; h<hitsInClus.size(); h++)  {
				if(hitsInClus.get(h).get_Layer() == l+1)
					nlayers[l]++;
			}
		}

		// count n. layers hit
		int nlayers_hit = 0;
		for(int l=0; l<nlayr; l++)
			if(nlayers[l]>0)
				nlayers_hit++;

		return nlayers_hit;
	}

	/**
	 *
	 * @param hit the hit
	 * @return  a selection cut to pass the hit (for now pass all hits)
	 */
	public boolean passHitSelection(Hit hit) {

		return true;
	}

	/**
	 * A method to select the largest cluster among a set of clusters with 4 or more of overlaping hits
	 * @param thisclus the cluster to be compared to a list of other clusters
	 * @param clusters the list of clusters
	 * @return the selected cluster
	 */
	public FittedCluster bestCluster(FittedCluster thisclus, List<FittedCluster> clusters) {

		List<FittedCluster> overlapingClusters = new ArrayList<FittedCluster>();

		for(FittedCluster cls : clusters) {

			List<FittedHit> hitOvrl = new ArrayList<FittedHit>();
			for(FittedHit hit : thisclus) {
				if(cls.contains(hit)) {

					if(!(hitOvrl.contains(hit)) ) {
						hitOvrl.add(hit);
					}
				}
			} // end loop over hits in thisclus

			//test
			boolean passCls = true;
			for(FittedCluster ovr : overlapingClusters) {
				if(ovr.get_Id()==cls.get_Id()) {
					passCls = false;
					continue;
				}
				//ensure that the lines are consistent

				if(Math.abs(ovr.get_clusterLineFitSlope()-cls.get_clusterLineFitSlope())>0.2)
					passCls = false;
			}
			if(hitOvrl.size()<3)
				passCls=false;

			if(passCls)
				overlapingClusters.add(cls);

		}



		Collections.sort(overlapingClusters);

		// return the largest cluster.
		return overlapingClusters.get(0);

	}

	/**
	 * @param allhits the list of unfitted hits
	 * @return layer efficiencies
	 * .....
	 */
	public EvioDataBank getLayerEfficiencies(List<Hit> allhits, EvioDataEvent event) {

		ClusterFinder gcf;
		
		int[][][] EffArray = new int[6][6][6]; //6 sectors,  6 superlayers, 6 layers
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				for(int k=0; k<6; k++)
					EffArray[i][j][k]=-1;
		
		for(int rejLy=1; rejLy<=6; rejLy++) {
			
			gcf = new ClusterFinder();
			
			//fill array of hit
			gcf.fillHitArray(allhits, rejLy);		
			//find clumps of hits
			List<Cluster> clusters = gcf.findClumps(allhits);
			// create cluster list to be fitted
			List<FittedCluster> selectedClusList =  new ArrayList<FittedCluster>();
	
			for(Cluster clus : clusters) {
				if(passSelector(clus)) {
					//System.out.println(" I passed this cluster "+clus.printInfo());
					FittedCluster fclus = new FittedCluster(clus);
					
					selectedClusList.add(fclus);
				}
			}
	
			
			for(FittedCluster clus : selectedClusList) {
				if(clus!=null) {
					
					int status = 0;
					//fit
					clus.clusterFitter("HitBased", false);
					for(Hit hit : allhits) {
						
						if(hit.get_Sector()!=clus.get_Sector() || hit.get_Superlayer()!=clus.get_Superlayer() || hit.get_Layer()!=rejLy)
							continue;
						
						double locX = hit.calcLocY(hit.get_Layer(), hit.get_Wire());
						double locZ = hit.get_Layer();
						
						double calc_doca = Math.abs(locX-clus.get_clusterLineFitSlope()*locZ-clus.get_clusterLineFitIntercept());
						
						if(calc_doca<2)
							status =1; //found a hit close enough to the track to assume that the layer is live
						
						int sec = clus.get_Sector()-1;
						int slay = clus.get_Superlayer()-1;
						int lay = rejLy -1;
						
						EffArray[sec][slay][lay] = status;
						
					}
				}
			}
		}
		// now fill the bank
		int bankSize =6*2*6;
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("HitBasedTrkg::LayerEffs",bankSize);
		int bankEntry = 0;
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				for(int k=0; k<6; k++) {
					bank.setInt("sector",bankEntry, i+1);
					bank.setInt("superlayer",bankEntry, j+1);
					bank.setInt("layer",bankEntry, k+1);
					bank.setInt("status", bankEntry,EffArray[i][j][k]);
					bankEntry++;
				}
		return bank;
		
	}
}
