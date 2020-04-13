import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocCongTac {
	private static int row;
	private static int col;
	private static DecimalFormat df = new DecimalFormat("#.##");

	private static double[] avaregeOfColumn(int[][] matrix) {
		int row = matrix.length;
		int col = matrix[0].length;

		double[] avarageArray = new double[col];

		int count = 0;
		int sumOfEachCol = 0;
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < row; j++) {
				if (matrix[j][i] >= 0) {
					sumOfEachCol += matrix[j][i];
					count++;
				}
			}

			avarageArray[i] = sumOfEachCol * 1.0 / count;
			count = 0;
			sumOfEachCol = 0;
		}
		return avarageArray;
	}

	private static double[][] normalizeMatrix(int[][] matrix, double[] avarageArray) {
		double[][] res = new double[row][col];
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < row; j++) {
				if (matrix[j][i] >= 0) {
					res[j][i] = matrix[j][i] - avarageArray[i];
				} else {
					res[j][i] = 0;
				}
			}
		}
		printMatrix(res);
		return res;
	}

	private static double[][] similarityMatrix(double[][] matrix) {
		double[][] similarityMatrix = new double[col][col];
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < col; j++) {
				similarityMatrix[i][j] = cosinVector(i, j, matrix);
			}
		}
		printMatrix(similarityMatrix);
		return similarityMatrix;
	}

	private static double cosinVector(int c1, int c2, double[][] matrix) {
		if (c1 == c2)
			return 1.0;
		double denominator1 = 0;
		double denominator2 = 0;
		double numerator = 0;

		for (int i = 0; i < row; i++) {
			numerator += matrix[i][c1] * matrix[i][c2];
			denominator1 += matrix[i][c1] * matrix[i][c1];
			denominator2 += matrix[i][c2] * matrix[i][c2];
		}

		return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
	}

	private static double[][] ratingPredictionMatrix(double[] avarageArray, double[][] normalizeMatrix,
			double[][] similarityMatrix) {

		double[][] res = new double[row][col];

		List<Integer> userRated = new ArrayList<Integer>();
		List<Integer> userNotRated = new ArrayList<Integer>();

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {

				if (normalizeMatrix[i][j] == 0) {
					userNotRated.add(j);
				} else {
					userRated.add(j);
					res[i][j] = normalizeMatrix[i][j];
				}
			}

			for (int userNotR : userNotRated) {
				res[i][userNotR] = getRating(userNotR, i, userRated, similarityMatrix, normalizeMatrix);
			}
			userRated.clear();
			userNotRated.clear();
		}
		//printMatrix(res);

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				res[i][j] += avarageArray[j];
			}
		}

		//printMatrix(res);
		return res;

	}

	private static double getRating(int user, int item, List<Integer> userRated, double[][] similarityMatrix,
			double[][] normalizeMatrix) {
		if (userRated.size() == 0)
			return -1;
		else if (userRated.size() == 1)
			return normalizeMatrix[item][userRated.get(0)];
		else {
			double max1 = -999;
			double max2 = -999;
			int userMax1 = -1;
			int userMax2 = -1;
			for (int i : userRated) {
				if (similarityMatrix[user][i] > max1 && i != user) {
					max2 = max1;
					max1 = similarityMatrix[user][i];
					userMax2 = userMax1;
					userMax1 = i;
				} else if (similarityMatrix[user][i] > max2 && i != user) {
					userMax2 = i;
					max2 = similarityMatrix[user][i];
				}
			}

			return (max1 * normalizeMatrix[item][userMax1] + max2 * normalizeMatrix[item][userMax2])
					/ (Math.abs(max1) + Math.abs(max2));
		}
	}

	private static double[][] collaborativeFilteringBaseUser(int[][] matrix) {
		col = matrix[0].length;
		row = matrix.length;

		double[] avg = avaregeOfColumn(matrix);
		double[][] nor = normalizeMatrix(matrix, avg);
		double[][] simi = similarityMatrix(nor);
		return ratingPredictionMatrix(avg, nor, simi);
	}

	private static double[][] collaborativeFilteringBaseItem(int[][] matrix) {
		int[][] newMatrix = convertMatrix(matrix);
		double[] avg = avaregeOfColumn(newMatrix);
		double[][] nor = normalizeMatrix(newMatrix, avg);
		double[][] simi = similarityMatrix(nor);
		return ratingPredictionMatrix(avg, nor, simi);
	}

	private static int[][] convertMatrix(int[][] matrix) {
		row = matrix[0].length;
		col = matrix.length;
		int[][] res = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				res[i][j] = matrix[j][i];
			}
		}
		return res;
	}

	public static void main(String[] args) {
		int[][] matrix = { { 5, 5, 2, 0, 1, -1, -1 }, { 4, -1, -1, 0, -1, 2, -1 }, { -1, 4, 1, -1, -1, 1, 1 },
				{ 2, 2, 3, 4, 4, -1, 4 }, { 2, 0, 4, -1, -1, -1, 5 } };
		
		int[][] matrix2 = {{1, 4, 5, -1, 3}, 
				{5, 1, -1, 5, 2}, 
				{4, 1, 2, 5, -1}, 
				{-1, 3, 4, -1, 4}};

		printMatrix(collaborativeFilteringBaseItem(matrix2));
		
	}

	private static void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(String.format("%1$7s", df.format(matrix[i][j])));

			}
			System.out.println();
		}

		System.out.println("========================================================");
	}

}
