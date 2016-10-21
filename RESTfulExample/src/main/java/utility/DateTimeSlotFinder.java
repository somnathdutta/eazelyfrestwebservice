package utility;

import java.util.Arrays;

public class DateTimeSlotFinder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String timeSlotValue = "01:00 PM - 02:00 PM";
		String dateValue = "2016-07-26";
		System.out.println(Arrays.toString(findDateTime(dateValue, timeSlotValue)));
	}

	public static String[] findDateTime(String dateValue, String timeSlotValue){
		String returnedValue[] = new String[2];
		String[] times = timeSlotValue.split("-");
		for(int i=0;i<times.length;i++){
			returnedValue[i] = dateValue+" "+times[i].trim();
		}
		return returnedValue;
	}
}
