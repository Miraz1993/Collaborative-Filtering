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
		//System.out.println("userSum "+userSum+" "+denom);
		Double weight=0.0;
		if(denom!=0.0) {
			weight=(Double)userSum/denom;
		}
		//System.out.println("weight "+weight);

		return weight;

	}
	public static HashSet<Integer> getCommonMovieRatings(HashMap<Integer, HashSet<Integer>> perUserMovies,Integer userId,Integer user,HashMap<Integer, HashMap<Integer, Double>> perEntryMap){
		//Set<Integer> =perUserMovies.keySet();
		//ArrayList<String> commonMovies=new ArrayList<>();
		ArrayList<Double> ratingList1=new ArrayList<>();
		ArrayList<Double> ratingList2=new ArrayList<>();
		HashSet<Integer> users;
		HashSet<Integer> movies1=perUserMovies.get(user);
		HashSet<Integer> movies2=perUserMovies.get(userId);
		/*for(Integer movie:movies) {
			users=userMovieMap.get(movie);
			//users.r
			if(users.contains(userId) && users.contains(user)) {
				ratingList1.add(perEntryMap.get(userId).get(movie));
				ratingList2.add(perEntryMap.get(user).get(movie));
			}


		}*/
		movies1.retainAll(movies2);

		//ArrayList<ArrayList<Double>> ratingLists=new ArrayList<>();
		//ratingLists.add(ratingList1);
		//ratingLists.add(ratingList2);

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
		for(Integer user:users) {
			weight=0.0;
			useravg2=0.0;



			if(perUserAvg.get(user)!=null) {
				useravg2=perUserAvg.get(user);
			}

			/*if(storedWeight.get(userId)!=null && storedWeight.get(userId).get(user)!=null) {
				weight=storedWeight.get(userId).get(user);
			}
			else if(storedWeight.get(user)!=null && storedWeight.get(user).get(userId)!=null) {
				weight=storedWeight.get(user).get(userId);
			}
			else {*/

			commonMovies=getCommonMovieRatings(perUserMovies,userId,user,perEntryMap);

			/*for(String movie:commonMovies) {
				ratingList1.add(perEntryMap.get(movie+":"+userId));
				ratingList2.add(perEntryMap.get(movie+":"+user));



			}*/

			weight=calculateWeight(commonMovies, useravg1, useravg2,perEntryMap,userId,user);
			/*HashMap<Integer, Double> weightMap=storedWeight.get(userId);
			if(weightMap==null)
				weightMap=new HashMap<>();

			storedWeight.put(userId,weightMap);*/
		//}
		k+=Math.abs(weight);
		vij=perEntryMap.get(user).get(movieId);
		if(vij==null) {
			vij=0.0;
		}
		//System.out.println("weight "+weight+" "+vij+" "+useravg2);
		acc+=(weight*(vij-useravg2));
		}
		Double div=0.0;
		if(k!=0.0) {
			div=(Double)acc/k;
		}
		Double rating=useravg1+div;
		//System.out.println("useravg1 "+useravg1+" "+k+" "+acc);
		//System.out.println(rating);
		return rating;

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String movieTitlesPath="";
		//String traningPath="C:\\Users\\miraz\\Desktop\\demo\\TrainingRatings.csv";
		//String testPath="C:\\Users\\miraz\\Desktop\\demo\\TestingRatings.csv";
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
			//Set<HashMap<String, Double>> 
			HashMap<Integer, HashMap<Integer, Double>> perEntryMap=new HashMap<>();
			HashMap<Integer, ArrayList<Double>> perUserMap=new HashMap<>();
			HashMap<Integer, Double> perUserAvg=new HashMap<>();
			HashMap<Integer, HashSet<Integer>> userMovieMap=new HashMap<>();
			HashMap<Integer, HashSet<Integer>> perUserMovies=new HashMap<>();
			//int i=0;
			ArrayList<Double> list;
			HashSet<Integer> movielist;
			HashSet<Integer> userlist;
			while ((line = bufferedReader.readLine()) != null) {
				//i++;
				strArr=line.split(",");
				//perEntryMap.put(strArr[0]+":"+strArr[1], Double.parseDouble(strArr[2]));
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
			//System.out.println("i "+i);
			Set<Integer> userSet=perUserMap.keySet();
			//ArrayList<Double> list;
			Double avg;
			for(Integer in:userSet) {
				list=perUserMap.get(in);
				avg=CollabFiltering.getAvg(list);
				perUserAvg.put(in, avg);
			}
			/*System.out.println(perUserAvg.get(573364));
			System.out.println(perUserAvg.get(2149668));
			System.out.println(perUserAvg.get(1089184));
			System.out.println(perUserAvg.get(2465894));
			System.out.println(perUserAvg.get(534508));*/

			//break;


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
				//System.out.println("pValue "+pValue+" "+strArr[2]);
				//if(i==100)
				//	break;
				//break;

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
