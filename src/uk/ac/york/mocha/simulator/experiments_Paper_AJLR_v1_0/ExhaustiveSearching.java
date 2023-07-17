package uk.ac.york.mocha.simulator.experiments_Paper_AJLR_v1_0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.york.mocha.simulator.entity.Node;

public class ExhaustiveSearching {

	static List<String> combinations = new ArrayList<>();

	public static void main(String args[]) {

		List<List<Node>> nodes = new ArrayList<>();
		nodes.add(getNodes(1));
		nodes.add(getNodes(2));
		nodes.add(getNodes(1));
		nodes.add(getNodes(2));

		String str = "";

		for (int i = 0; i < nodes.size(); i++) {
			str += i;
		}

		// str = "12345";

		System.out.println(str);
		int n = str.length();
		ExhaustiveSearching permutation = new ExhaustiveSearching();
		permutation.permute(str, 0, n - 1);

		System.out.println(combinations.size());

		List<String> releaseOne = new ArrayList<>(combinations);
		List<String> releaseTwo = new ArrayList<>(combinations);

		List<String> allExecutions = new ArrayList<>();

		for (int i = 0; i < releaseOne.size(); i++) {
			for (int j = 0; j < releaseTwo.size(); j++) {
				String exe = releaseOne.get(i);
				exe += releaseTwo.get(j);
				allExecutions.add(exe);
			}
		}

		allExecutions.forEach(c -> System.out.println(c));
		System.out.println("num: " + allExecutions.size());
	}

	/**
	 * permutation function
	 * 
	 * @param str
	 *            string to calculate permutation for
	 * @param l
	 *            starting index
	 * @param r
	 *            end index
	 */
	private void permute(String str, int l, int r) {
		if (l == r) {
			// System.out.println(str);
			combinations.add(str);
		} else {
			for (int i = l; i <= r; i++) {
				str = swap(str, l, i);
				permute(str, l + 1, r);
				str = swap(str, l, i);
			}
		}
	}

	/**
	 * Swap Characters at position
	 * 
	 * @param a
	 *            string value
	 * @param i
	 *            position 1
	 * @param j
	 *            position 2
	 * @return swapped string
	 */
	public String swap(String a, int i, int j) {
		char temp;
		char[] charArray = a.toCharArray();
		temp = charArray[i];
		charArray[i] = charArray[j];
		charArray[j] = temp;
		return String.valueOf(charArray);
	}

	// This code is contributed by Mihir Joshi

	public static List<Node> getNodes(int num) {
		Random rng = new Random(1000);

		List<Long> wcet = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			wcet.add((long) rng.nextInt(5000));
		}

		wcet.sort((c1, c2) -> Long.compare(c1, c2));

		List<Node> nodes = new ArrayList<>();

		if (num == 1) {
			Node n0_0 = new Node(wcet.get(0), -1, null, 0, 0, null, false, rng);
			Node n0_1 = new Node(wcet.get(1), -1, null, 1, 0, null, false, rng);
			Node n0_2 = new Node(wcet.get(2), -1, null, 2, 0, null, false, rng);
			Node n0_3 = new Node(wcet.get(3), -1, null, 3, 0, null, false, rng);
			Node n0_4 = new Node(wcet.get(4), -1, null, 4, 0, null, false, rng);
			Node n0_5 = new Node(wcet.get(5), -1, null, 5, 0, null, false, rng);

			nodes.add(n0_0);
			nodes.add(n0_1);
			nodes.add(n0_2);
			nodes.add(n0_3);
			nodes.add(n0_4);
			nodes.add(n0_5);
		}

		if (num == 2) {

			Node n1_0 = new Node(wcet.get(6), -1, null, 0, 1, null, false, rng);
			Node n1_1 = new Node(wcet.get(7), -1, null, 1, 1, null, false, rng);
			Node n1_2 = new Node(wcet.get(8), -1, null, 2, 1, null, false, rng);
			Node n1_3 = new Node(wcet.get(9), -1, null, 3, 1, null, false, rng);

			nodes.add(n1_0);
			nodes.add(n1_1);
			nodes.add(n1_2);
			nodes.add(n1_3);

		}

		nodes.forEach(n -> {
			n.start = 0;
			System.out.println(n.toString());
		});

		return nodes;

	}
}
