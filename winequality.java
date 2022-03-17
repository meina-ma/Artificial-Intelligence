import java.io.*;
import java.util.*;
public class winequality{
	
	public static TreeNode DTL(double[][] array,int minleaf){
		if(array.length==0){
			return null;
		}

		int line = array.length;
		int column = array[0].length;
		int []quality = new int[line];

		for(int i = 0;i<line;i++){
			quality[i] = (int)array[i][column-1];
		}
		//base case
		if(line<=minleaf){
			TreeNode t1 = new TreeNode();
			if(most_frequent(quality)!=-1){
				t1.label=most_frequent(quality);

			}else{
				t1.label = 0;
			}
			t1.left=null;
			t1.right=null;
			return t1;
		}
		//recursion
	
		int bestAttr = (int)ChooseSplit(array)[0];
		double splitPoint = ChooseSplit(array)[1];
		TreeNode root = new TreeNode();
		root.splitval = splitPoint;
		root.attribute = bestAttr;
		TreeNode left = DTL(leftArray(array,root.attribute,root.splitval),minleaf);
		TreeNode right= DTL(rightArray(array,root.attribute,root.splitval),minleaf);
		root.left =left;
		root.right = right;

		return root;
		

	}


	public static double[] ChooseSplit(double[][] array){
		double[]tmp = new double[2];
		double bestgain = 0.0,splitval = 0.0,gain = 0.0,bestsplitval=0.0;
		int bestattr = 0,attr=0;

		int line = array.length;//training sample number
		int column = 12;//11 attributes + 1 quality


		for(attr = 0;attr<11;attr++){//each attribute 0~10
			double[] attrArray = new double[line];//store every array of each attribute
			for(int j = 0;j<line;j++){
				attrArray[j] = array[j][attr];
				
			}

			Arrays.sort(attrArray);//Sort the array x[attr]

			for(int k = 0;k<line-1;k++){//calculating the split value
				splitval = 0.5*(attrArray[k]+attrArray[k+1]);
			
			    //dividing the array using split value
				int number1 = leftArray(array,attr,splitval).length;
				int number2 = rightArray(array,attr,splitval).length;

				//two branches number
				//P(c1) P(c2)
				double p1 = (double)number1/line;
				double p2 = (double)number2/line;

				double I1 = calcu_I(getoneColumn(array,11));
				double I2 = 0;
				double I3 = 0;
				if(number1!=0){
					I2 = calcu_I(getoneColumn(leftArray(array,attr,splitval),11));
				}	
				
				if(number2!=0){
					I3 = calcu_I(getoneColumn(rightArray(array,attr,splitval),11));
				}

				//information gain of each attribute
				gain = I1-p1*I2-p2*I3;
				if(gain>bestgain){
					bestattr=attr;
					bestsplitval=splitval;
					bestgain=gain;
				}
			}
		}
		tmp[0]=(double)bestattr;
		tmp[1]=bestsplitval;
		System.out.println(tmp[0]+" "+tmp[1]);
		return tmp;
	}

	public static double calcu_I(int[] array){
		int []count = new int[3];
		int len = array.length;
		double ans = 0.0;

		for(int i = 0;i<len;i++){
			if(array[i]==5){
				count[0]++;
			}else if(array[i]==6){
				count[1]++;
			}else{
				count[2]++;
			}
		}
		for(int i = 0;i<3;i++){
			if(count[i]!=0){
				ans+=((double)count[i]/len)*(Math.log((double)len/count[i])/Math.log(2));
			}
		}
		return ans;
	}

	public static int[] getoneColumn(double[][]array,int x){
		int []ans = new int[array.length];
		for(int i=0;i<array.length;i++){
			ans[i] = (int)array[i][x];
		}
		return ans;
	}

	public static int PredictDTL(TreeNode n, double[]test){
		while(n.left!=null||n.right!=null){
			if(test[n.attribute]<=n.splitval){
				n=n.left;
			}else{
				n=n.right;
			}
		}
		return n.label;


	}

	public static int most_frequent(int[]array){
		Map<Integer,Integer> map = new HashMap<>();
		int count = 0;
		for(int n:array){
			count=map.getOrDefault(n,0);
			map.put(n,count+1);
		}
		Collection<Integer> countmap = map.values();
		int maxCount = Collections.max(countmap);
		int maxnum = 0;
		int count1 = 0;
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			if (maxCount == entry.getValue()) {
				maxnum = entry.getKey();
				count1++;
			}
		}
		if(count1==1){
			return maxnum;
		}else{
			return -1;
		}
		
	}

	public static double[][] leftArray(double[][]array,int attr,double splitval){
		int count = 0;
		for(int i = 0;i<array.length;i++){
			if(array[i][attr]<=splitval){
				count++;
			}
		}
		double [][]res = new double[count][array[0].length];
		int count_left = 0;
		for(int i = 0;i<array.length;i++){
			if(array[i][attr]<=splitval){
				for(int j=0;j<12;j++){
					res[count_left][j] = array[i][j];
				}
				count_left++;
			}
		}
		return res;
	}

	public static double[][] rightArray(double[][]array,int attr,double splitval){
		int count = 0;
		for(int i = 0;i<array.length;i++){
			if(array[i][attr]>=splitval){
				count++;
			}
		}
		double [][]res = new double[count][array[0].length];
		int count_right = 0;
		for(int i = 0;i<array.length;i++){
			if(array[i][attr]>=splitval){
				for(int j=0;j<array[0].length;j++){
					res[count_right][j] = array[i][j];
				}
				count_right++;
			} 
		}
		return res;
	}

	public static double[][] setZero(double[][]array,int attr){
		for(int i = 0;i<array.length;i++){
			array[i][attr]=-1.0;
		}
		return array;

	}

	public static void main(String[] args){
		int minleaf = Integer.parseInt(args[2]);
		//training data
		FileReader readerTrain = null;
		try {
			readerTrain = new FileReader(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bf1 = new BufferedReader(readerTrain);
		String lineContent1 = null;//store each line
		List<String> list1 = new ArrayList<>();
		try {
			while((lineContent1=bf1.readLine())!=null){
				list1.add(lineContent1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		list1.remove(0);
		int lineNum1 = list1.size();
		double[][] array1 = new double[lineNum1][12];
		int count1 = 0;
		
		for(String str:list1){
			String[]strs = str.split(" ");
			int h1 = 0;
			for(int i = 0;i<strs.length;i++){
				if(!strs[i].equals("")){
					array1[count1][h1] = Double.valueOf(strs[i]);
					h1++;
				}
			}
			count1++;
		}
		try {
			readerTrain.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bf1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileReader readerTest = null;
		try {
			readerTest = new FileReader(args[1]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bf2 = new BufferedReader(readerTest);
		String lineContent2 = null;//store each line
		List<String> list2 = new ArrayList<>();
		try {
			while((lineContent2=bf2.readLine())!=null){
				list2.add(lineContent2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		list2.remove(0);
		int lineNum2 = list2.size();
		double[][] array2 = new double[lineNum1][11];
		int count2 = 0;
		
		for(String str:list2){
			String[]strs = str.split(" ");
			int h2 = 0;
			for(int i = 0;i<strs.length;i++){
				if(!strs[i].equals("")){
					array2[count2][h2] = Double.valueOf(strs[i]);
					h2++;
				}
			}
			count2++;
		}
		try {
			readerTest.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bf2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TreeNode n = DTL(array1,minleaf);
		
		double []testingdata = new double[11];
		for(int i = 0;i<array2.length;i++){
			for(int j = 0;j<11;j++){
				testingdata[j] = array2[i][j];
			}
			int result = PredictDTL(n,testingdata);
			System.out.println(result);

		}
		

	}
}
//Tree class
	class TreeNode{
		public TreeNode left;
		public TreeNode right;
		public double splitval;
		public int attribute;
		public int label;
		TreeNode(){
			splitval = 0.0;
			attribute = 0;
			label = 0;
		}
		

	}
