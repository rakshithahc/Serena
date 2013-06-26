package com.serana;

import java.text.SimpleDateFormat;
import java.util.Comparator;

public class Sorter {

	static final Comparator<All_Request_data_dto> byDate = new Comparator<All_Request_data_dto>() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

		public int compare(All_Request_data_dto ord1, All_Request_data_dto ord2) {
			java.util.Date d1 = null;
			java.util.Date d2 = null;
			try {
				d1 = sdf.parse(ord1.lastModifiedDate);
				d2 = sdf.parse(ord2.lastModifiedDate);

			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			// return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
		}
	};
	static final Comparator<All_Request_data_dto> byDate1 = new Comparator<All_Request_data_dto>() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

		public int compare(All_Request_data_dto ord1, All_Request_data_dto ord2) {
			java.util.Date d1 = null;
			java.util.Date d2 = null;
			try {
				d1 = sdf.parse(ord1.submitDate);
				d2 = sdf.parse(ord2.submitDate);

			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			// return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
		}
	};
}
