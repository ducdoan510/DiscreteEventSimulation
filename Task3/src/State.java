import java.util.*;
import java.io.*;
import java.lang.*;

class State extends GlobalSimulation{
	
	// Here follows the state variables and other variables that might be needed
	// e.g. for measurements
	private static double q1ServiceMeanTime = 1, q2ServiceMeanTime = 1, measureMeanTime = 5;
	public int numberInQ1 = 0, numberInQ2 = 0, curCustomersSum = 0, noMeasurements = 0, noArrivals = 0;
	public double curTimeInSystemSum = 0;
	private double interArrTime;
	private int nextCustQ1 = 0, nextCustQ2 = 0;
	private List<Double> customerEnterTime = new ArrayList<Double>();
	private List<Integer> customerStatus = new ArrayList<Integer>();

	Random slump = new Random(); // This is just a random number generator
	
	public State(double interArrTime) {
		this.interArrTime = interArrTime;
	}
	
	// The following method is called by the main program each time a new event has been fetched
	// from the event list in the main loop. 
	public void treatEvent(Event x) {
		 x.show();
		switch (x.eventType){
			case ARRIVEQ1:
				arrivalq1(x);
				break;
			case DEPARTQ1:
				departq1(x);
				break;
			case DEPARTQ2:
				departq2(x);
				break;
			case MEASURE:
				measure();
				break;
		}
	}
	
	
	// The following methods defines what should be done when an event takes place. This could
	// have been placed in the case in treatEvent, but often it is simpler to write a method if 
	// things are getting more complicated than this.
	
	private void arrivalq1(Event x){
		// System.out.printf("------%f %d\n", x.eventTime, x.custId);
		customerEnterTime.add(x.eventTime);
		noArrivals++;
		numberInQ1++;
		if (numberInQ1 == 1) {
			insertEvent(DEPARTQ1, time + exp(q1ServiceMeanTime), nextCustQ1);
		}
		insertEvent(ARRIVEQ1, time + exp(interArrTime), noArrivals);
	}
	
	private void departq1(Event x){
		numberInQ1--;
		numberInQ2++;
		nextCustQ1++;
		if (numberInQ2 == 1) {
			insertEvent(DEPARTQ2, time + exp(q2ServiceMeanTime), nextCustQ2);
		}
		
		if (numberInQ1 > 0) {
			insertEvent(DEPARTQ1, time + exp(q1ServiceMeanTime), nextCustQ1);
		}
	}
	
	private void departq2(Event x) {
		numberInQ2--;
		nextCustQ2++;
		if (x.custId < customerEnterTime.size())
			curTimeInSystemSum += (x.eventTime - customerEnterTime.get(x.custId));
		// System.out.printf("+++++++%f %d\n", x.eventTime, x.custId);
		if (numberInQ2 > 0) {
			// Event eventInQ2 = findEvent(DEPARTQ1);
			insertEvent(DEPARTQ2, time + exp(q2ServiceMeanTime), nextCustQ2);
		}
	}
	
	private void measure() {
		curCustomersSum += (numberInQ1 + numberInQ2);
		noMeasurements++;
		insertEvent(MEASURE, time + exp(measureMeanTime), -1);	
	}
	
	public double exp(double mean) {
		return -Math.log(1-slump.nextDouble())*mean;
	}
}