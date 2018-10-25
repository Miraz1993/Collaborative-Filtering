package HW3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CollabFiltering {
	public static Double getAvg(ArrayList<Double> list) {
		int size=list.size();
		Double sum=0.0,avg;
		for(Double d:list) {
			sum+=d;
		}
		avg=(Double)sum/size;
		return avg;
	}
	public static Double calculateWeight(HashSet<Integer> commonMovies,Double useravg1,Double useravg2, HashMap<Integer, HashMap<Integer, Double>> perEntryMap, Integer user1, Integer user2) {
		Double userSum=0.0;
		//Double user2Sum=0.0;
		Double user1SumSquare=0.0;
		Double user2SumSquare=0.0;
		Double diff1;
		Double diff2;
		for(Integer in:commonMovies) {
			diff1=perEntryMap.get(user1).get(in)-useravg1;
			diff2=perEntryMap.get(user2).get(in)-useravg2;
			userSum+=(diff1*diff2);

			user1SumSquare+=(Math.pow(diff1, 2));
			user2SumSquare+=(Math.pow(diff2, 2));
		}
		Double denom=Math.sqrt(user1SumSquare*user2SumSquare);
		
		Double weight=0.0;
		if(denom!=0.0) {
			weight=(Double)userSum/denom;
		}
		

		return weight;

	}
	public static HashSet<Integer> getCommonMovieRatings(HashMap<Integer, HashSet<Integer>> perUserMovies,Integer userId,Integer user,HashMap<Integer, HashMap<Integer, Double>> perEntryMap){
		
		ArrayList<Double> ratingList1=new ArrayList<>();
		ArrayList<Double> ratingList2=new ArrayList<>();
		HashSet<Integer> users;
		HashSet<Integer> movies1=perUserMovies.get(user);
		HashSet<Integer> movies2=perUserMovies.get(userId);
		
		movies1.retainAll(movies2);

		

		return movies1;
	}
	public static Double predictScore(String[] strArr,HashMap<Integer, HashMap<Integer, Double>> perEntryMap,HashMap<Integer, Double> perUserAvg,HashMap<Integer, HashSet<Integer>> userMovieMap,HashMap<Integer, HashSet<Integer>> perUserMovies) {

		Integer movieId=Integer.parseInt(strArr[0]);
		Integer userId=Integer.parseInt(strArr[1]);
		//System.out.println(movieId+" "+userId);
		Set<Integer> users=userMovieMap.get(movieId);
		HashSet<Integer> commonMovies;
		Double k=0.0;
		Double acc=0.0;
		Double useravg1=0.0;
		if(perUserAvg.get(userId)!=null) {
			useravg1=perUserAvg.get(userId);
		}
		HashMap<Integer, HashMap<Integer, Double>> storedWeight=new HashMap<>();
		Double weight;
		Double useravg2;
		ArrayList<Double> ratingList1;
		ArrayList<Double> ratingList2;
		Double vij;
		for(Integer user:users) 
		{
			
			weight=0.0;
			useravg2=0.0;



			if(perUserAvg.get(user)!=null) {
				useravg2=perUserAvg.get(user);
			}


			commonMovies=getCommonMovieRatings(perUserMovies,userId,user,perEntryMap);


			weight=calculateWeight(commonMovies, useravg1, useravg2,perEntryMap,userId,user);

			k+=Math.abs(weight);
			vij=perEntryMap.get(user).get(movieId);
			if(vij==null) {
				vij=0.0;
			}
			
			acc+=(weight*(vij-useravg2));
		
		}
		Double div=0.0;
		if(k!=0.0) {
			div=(Double)acc/k;
		}
		Double rating=useravg1+div;
		
		return rating;

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String traningPath=args[0];
		String testPath=args[1];

		BufferedReader bufferedReader = null;
		try {
			bufferedReader=new BufferedReader(new FileReader(traningPath));
			Calendar cal = Calendar.getInstance();
			long time1=cal.getTimeInMillis();
			System.out.println();
			String line="";
			String[] strArr;
			
			HashMap<Integer, HashMap<Integer, Double>> perEntryMap=new HashMap<>();
			HashMap<Integer, ArrayList<Double>> perUserMap=new HashMap<>();
			HashMap<Integer, Double> perUserAvg=new HashMap<>();
			HashMap<Integer, HashSet<Integer>> userMovieMap=new HashMap<>();
			HashMap<Integer, HashSet<Integer>> perUserMovies=new HashMap<>();
			
			ArrayList<Double> list;
			HashSet<Integer> movielist;
			HashSet<Integer> userlist;
			while ((line = bufferedReader.readLine()) != null) {
				
				strArr=line.split(",");
				
				Integer userId=Integer.parseInt(strArr[1]);
				Integer movieId=Integer.parseInt(strArr[0]);
				Double rating=Double.parseDouble(strArr[2]);
				if(perUserMap.containsKey(userId)) {
					list=perUserMap.get(userId);
					list.add(rating);
					perUserMap.put(userId, list);
					movielist=perUserMovies.get(userId);
					movielist.add(movieId);
					perUserMovies.put(userId, movielist);
					HashMap<Integer, Double> ratingMap=perEntryMap.get(userId);
					ratingMap.put(movieId, rating);
					perEntryMap.put(userId, ratingMap);

				}
				else {
					list=new ArrayList<>();
					list.add(Double.parseDouble(strArr[2]));
					perUserMap.put(userId, list);
					movielist=new HashSet<>();
					movielist.add(movieId);
					perUserMovies.put(userId, movielist);
					HashMap<Integer, Double> ratingMap=new HashMap<>();
					ratingMap.put(movieId, rating);
					perEntryMap.put(userId, ratingMap);

				}
				if(userMovieMap.containsKey(movieId)) {
					userlist=userMovieMap.get(movieId);
					userlist.add(userId);
					userMovieMap.put(movieId, userlist);
				}
				else {
					userlist=new HashSet<>();
					userlist.add(userId);
					userMovieMap.put(movieId, userlist);
				}

			}
			
			Set<Integer> userSet=perUserMap.keySet();
			
			Double avg;
			for(Integer in:userSet) {
				list=perUserMap.get(in);
				avg=CollabFiltering.getAvg(list);
				perUserAvg.put(in, avg);
			}
			


			Double rme=0.0;
			Double absl=0.0;
			bufferedReader=new BufferedReader(new FileReader(testPath));
			Double pValue;
			Double diff;
			int i=0;
			while ((line = bufferedReader.readLine()) != null) {

				i++;
				strArr=line.split(",");

				pValue=CollabFiltering.predictScore(strArr, perEntryMap, perUserAvg, userMovieMap, perUserMovies);
				diff=pValue-Double.parseDouble(strArr[2]);
				rme+=(diff*diff);
				absl+=Math.abs(diff);
				

			}
			System.out.println("RMS error value "+Math.sqrt((Double)rme/i));
			System.out.println("Mean absolute error "+((Double)absl/i));
			long time2=cal.getTimeInMillis();
			System.out.println("time "+((long)(time2-time1)/1000));


		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

}
