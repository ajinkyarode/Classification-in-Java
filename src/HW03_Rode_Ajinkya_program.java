import java.io.BufferedReader;	
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Program to find a threshold for a police officer to set their laser speed detector at so that 
 * it beeps in such a way that it minimizes the total of (false alarms).
 * 
 * @author ajinkyarode
 *
 */
public class HW03_Rode_Ajinkya_program {

	/**
	 * Method to cut the decimal up to two digits
	 * 
	 * @param x
	 * 		decimal to be truncated
	 * @return
	 * 		truncated value
	 */
	public static double truncate(double x)
	{ 
		long y = (long)(x*100); 
		double z = (double)y/100; 
		return z; 
	}

	/**
	 * sd,fdjsbf
	 * @param args
	 */
	public static void main(String args[])
	{
		double false_alarm=0;
		double misses=0;
		double true_neg=1;
		double true_pos=1;
		double bin_no[]=new double[128];
		double start=30.5;
		double end=72.5;
		double true_pos_rate[]=new double[128];
		double false_pos_rate[]=new double[128];
		double miss_rate[]=new double[128];
		double total_no[]=new double[128];

		/*
		 * To read the ".CSV" file: "CLASSIFIED_TRAINING_SET_FOR_RECKLESS_DRIVERS.csv".
		 * 
		 * Note: Keep the above file in the same project in order to make
		 * it easy to read.
		 */
		BufferedReader fileReader = null;
		final String DELIMITER = ",";
		boolean firstLine = true;
		String file_name="CLASSIFIED_TRAINING_SET_FOR_RECKLESS_DRIVERS.csv";
		Double[] speeds_arr=new Double[128];
		Integer[] reckless_arr=new Integer[128];
		try
		{
			String line = "";
			fileReader = new BufferedReader(new FileReader(file_name));
			int p=0;
			while ((line = fileReader.readLine()) != null)
			{
				
				/*
				 * Remove the first row since it contains labels
				 */
				if (line.contains("SPEED")) {
					if (firstLine) {
						firstLine = false;
						continue;
					} 
				}
				String[] tokens = line.split(DELIMITER);
				speeds_arr[p]=Double.parseDouble(tokens[0]);
				reckless_arr[p]=Integer.parseInt(tokens[1]);
				// Uncomment the below part to display the speed and recklessness of drivers		
				/*System.out.println("[Speed=" + speeds_arr[p] 
                        + " , reckless=" + reckless_arr[p] + "]");*/
				p++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Rounding off the speeds to the nearest 0.5 value. 
		 */
		double first=speeds_arr[0];
		double second=speeds_arr[1];
		int count=0;
		for(double i=start; i<=end; i=i+0.5)
		{
			bin_no[count]=i;
			for(int j=0; j<speeds_arr.length; j++){
				if(speeds_arr[j]>i-0.25 && speeds_arr[j]<=i+0.25)
				{
					speeds_arr[j]=i;
				}	
			}
			count++;
		}

		/*
		 * Quantizing the speeds into bins of size 0.5 mph and calculating
		 * frequencies of each bin (histogram).
		 */
		double init=30.5;
		int binFreq[]=new int[84];
		int mo=0;
		while(init<=72.5)
		{
			for(int k=0;k<speeds_arr.length;k++)
			{

				if(speeds_arr[k].equals(init))
				{
					binFreq[mo]++;
				}		 
				continue;
			}
			mo++;
			init=init+0.5;
		}
		System.out.println("[Bin  --> Frequency]");
		for(int i=0;i<84;i++)
		{
			System.out.println("["+bin_no[i]+" --> "+ binFreq[i]+"]");
		}

		/*
		 * Calculating the threshold value.
		 * Calculating the true positives, true negatives, false positives and false negatives
		 * for the best threshold.
		 * 
		 * Note: Uncomment the commented part to check all the values.
		 */
		int total=0;
		for(int i=2;i<128;i++)
		{ 
			if(speeds_arr[i]<=first)
			{
				if(reckless_arr[i]==0)
				{
					true_neg++;
					//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" true_neg-> "+true_neg+" first-> "+first+" second-> "+second);
				}
				else if(reckless_arr[i]==1)
				{
					false_alarm++;
					//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" false_alarm-> "+false_alarm+" first-> "+first+" second-> "+second); 
				}
			}
			else if(speeds_arr[i]>=second)
			{
				if(reckless_arr[i]==1)
				{
					true_pos++;
					//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" true_pos-> "+true_pos+" first-> "+first+" second-> "+second); 
				}
				else if(reckless_arr[i]==0)
				{ 
					misses++;
					//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" misses-> "+misses+" first-> "+first+" second-> "+second); 
				}
			}
			else if(reckless_arr[i]==0)
			{
				first = speeds_arr[i];
				true_neg++;
				//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" true_neg-> "+true_neg+" first-> "+first+" second-> "+second);
			}else if(reckless_arr[i]==1)
			{
				second = speeds_arr[i];
				true_pos++;
				//System.out.println("speed-> "+speeds_arr[i]+" reckless-> "+reckless_arr[i]+" true_pos-> "+true_pos+" first-> "+first+" second-> "+second);
			}

			/*
			 * Calculating the true positive rate using the formula: "true_positive / true_positive + misses"
			 */
			true_pos_rate[i-2]=(true_pos)/(true_pos+misses);

			/*
			 * Calculating the false positive rate using the formula: "false_alarm / false_alarm + true_negative"
			 */
			false_pos_rate[i-2]=(false_alarm)/(false_alarm+true_neg); 

			/*
			 * Calculating the misclassification rate using the formula:
			 * misses + false_alarm / true_negative + true_positive + misses + false_alarm 
			 */
			if((misses+false_alarm)/(true_neg+true_pos+misses+false_alarm)!=0)
			{
				miss_rate[i-2]=(misses+false_alarm)/(true_neg+true_pos+misses+false_alarm);
				total_no[i-2]=total++;
			}
			else
			{
				continue;
			}
		}          

		/*
		 * Displaying the best threshold and misclassification rate for the same.
		 */
		System.out.println("Without Histogram Values(binning): \nTrue Negatives: "+(int)true_neg+"\nTrue_Positives: "+(int)true_pos+"\nMisses: "+(int)misses+"\nFalse Alarm: "+(int)false_alarm);
		double mis_rate=0;
		mis_rate=(misses+false_alarm)/(true_neg+true_pos+misses+false_alarm);
		System.out.println("Threshold: "+first);
		System.out.println("Misclassification Rate for "+first+": "+mis_rate);

		
		/*
		 *  THIS PART REQUIRES THE jFreeChart LIBRARIES IMPORTED IN YOUR
		 *  PROJECT TO WORK. 
		 *  
		 *  SEPARATE GRAPHS HAVE BEEN SUBMITTED IN THE "GRAPHS" FOLDER IN THE DROPBOX
		 *  
		 *  NOTE: IF YOU WANT TO VIEW THE GRAPHS, IMPORT THE LIBRARIES PROVIDED IN THE 
		 *        DROPBOX AND "UNCOMMENT THE BELOW ENTIRE SECTION".
		 */
		
		/*
		 
		 // Plotting the graph using jFreeChart library in Java.
		 
		XYSeries series1 = new XYSeries("Misclassification Rate");
		for(int y=0;y<84;y++){
			series1.add(bin_no[y],miss_rate[y]);
		}
		XYSeriesCollection dat1 = new XYSeriesCollection();
		dat1.addSeries(series1);
		JFreeChart chart1 = ChartFactory.createXYLineChart(
				"Misclassification Rate", 
				"Threshold",
				"Misclassification Rate", 
				dat1, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false
				);
		
		 // Saving the file as an image on the desktop
		 // 
		 // Note: Set the path of your desktop to generate the image on it.
		 
		try {
			ChartUtilities.saveChartAsJPEG(new File("/Users/ajinkyarode/Desktop/Misclassification Rate.jpg"), chart1, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart1.");
		}
		
		 // Plotting the graph using jFreeChart library in Java.
		 
		XYSeries series2 = new XYSeries("ROC Curve");
		for(int y=0;y<128;y++){
			series2.add(false_pos_rate[y],true_pos_rate[y]);	
		}	
		XYSeriesCollection dat2 = new XYSeriesCollection();
		dat2.addSeries(series2);
		JFreeChart chart2 = ChartFactory.createXYLineChart(
				"ROC Curve", // Title
				"False Alarm Rate (False Positive Rate)", 
				"Correct Hit Rate (True Positive Rate)", 
				dat2, 
				PlotOrientation.VERTICAL, 
				true,
				true,
				false 
				);

		
		 // Saving the file as an image on the desktop
		 // 
		 // Note: Set the path of your desktop to generate the image on it.
		 
		try {
			ChartUtilities.saveChartAsJPEG(new File("/Users/ajinkyarode/Desktop/ROC_Curve_without_cutting.jpg"), chart2, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart2.");
		}

		 // Cutting the decimal upto two digits for better graph.
		 
		for(int i=0;i<128;i++)
		{
			double temp=0;
			double tmp=0;
			temp=true_pos_rate[i];
			tmp=false_pos_rate[i];
			true_pos_rate[i]=truncate(temp);
			false_pos_rate[i]=truncate(tmp);
		}

		 // Plotting the graph using jFreeChart library in Java.
		 
		XYSeries series3 = new XYSeries("ROC Curve");
		for(int y=0;y<128;y++){
			series3.add(false_pos_rate[y],true_pos_rate[y]);	
		}
		XYSeriesCollection dat3 = new XYSeriesCollection();
		dat3.addSeries(series3);
		JFreeChart chart3 = ChartFactory.createXYLineChart(
				"ROC Curve", // Title
				"False Alarm Rate (False Positive Rate)", 
				"Correct Hit Rate (True Positive Rate)", 
				dat3, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false 
				);

		 // Saving the file as an image on the desktop
		 // 
		 // Note: Set the path of your desktop to generate the image on it.
		 
		try {
			ChartUtilities.saveChartAsJPEG(new File("/Users/ajinkyarode/Desktop/ROC_Curve_with_cutting.jpg"), chart3, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart3.");
		}
		 */
		
	}
}		
