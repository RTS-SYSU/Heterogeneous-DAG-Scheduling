package uk.ac.york.mocha.simulator.experiments_Paper_AJLR_v1_0;

import java.util.ArrayList;
import java.util.List;

import uk.ac.york.mocha.simulator.resultAnalyzer.OneSystemResults;

public class ResultCapsule {

	public List<OneSystemResults> allSys = new ArrayList<>();
	
	public synchronized void add(OneSystemResults res) {
		allSys.add(res);
	}
}
