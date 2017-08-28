//	----------------------------------------------------------
//	Copyright (c) 2017, Battelle Memorial Institute
//	All rights reserved.
//	----------------------------------------------------------

// package gov.pnnl.gridlabd.cim;

import java.io.*;
import org.apache.jena.query.*;
import java.text.DecimalFormat;

public class DistPowerXfmrMesh extends DistComponent {
	static final String szQUERY = 
		"SELECT ?pname ?fnum ?tnum ?r ?x WHERE {"+
		" ?p r:type c:PowerTransformer."+
		" ?p c:IdentifiedObject.name ?pname."+
		" ?from c:PowerTransformerEnd.PowerTransformer ?p."+
		" ?imp c:TransformerMeshImpedance.FromTransformerEnd ?from."+
		" ?imp c:TransformerMeshImpedance.ToTransformerEnd ?to."+
		" ?imp c:TransformerMeshImpedance.r ?r."+
		" ?imp c:TransformerMeshImpedance.x ?x."+
		" ?from c:TransformerEnd.endNumber ?fnum."+
		" ?to c:TransformerEnd.endNumber ?tnum."+
		"} ORDER BY ?pname ?fnum ?tnum";

	public String name;
	public int[] fwdg;
	public int[] twdg;
	public double[] r;
	public double[] x;
	public int size;

	private void SetSize (String pname) {
		size = 1;
		String szCount = "SELECT (count (?p) as ?count) WHERE {"+
			" ?p r:type c:PowerTransformer."+
			" ?p c:IdentifiedObject.name \"" + pname + "\"."+
			" ?from c:PowerTransformerEnd.PowerTransformer ?p."+
			" ?imp c:TransformerMeshImpedance.FromTransformerEnd ?from."+
			"}";
		ResultSet results = RunQuery (szCount);
		if (results.hasNext()) {
			QuerySolution soln = results.next();
			size = soln.getLiteral("?count").getInt();
		}
		fwdg = new int[size];
		twdg = new int[size];
		r = new double[size];
		x = new double[size];
	}

	public DistPowerXfmrMesh (ResultSet results) {
		if (results.hasNext()) {
			QuerySolution soln = results.next();
			String pname = soln.get("?pname").toString();
			name = GLD_Name (pname, false);
			SetSize (pname);
			for (int i = 0; i < size; i++) {
				fwdg[i] = Integer.parseInt (soln.get("?fnum").toString());
				twdg[i] = Integer.parseInt (soln.get("?tnum").toString());
				r[i] = Double.parseDouble (soln.get("?r").toString());
				x[i] = Double.parseDouble (soln.get("?x").toString());
				if ((i + 1) < size) {
					soln = results.next();
				}
			}
		}		
	}

	public String DisplayString() {
		DecimalFormat df = new DecimalFormat("#0.000000");
		StringBuilder buf = new StringBuilder ("");
		buf.append (name + " " + Integer.toString(size));
		for (int i = 0; i < size; i++) {
			buf.append ("\n  fwdg=" + Integer.toString(fwdg[i]) + " twdg=" + Integer.toString(twdg[i]) +
									" r=" + df.format(r[i]) + " x=" + df.format(x[i]));
		}
		return buf.toString();
	}

	public String GetKey() {
		return name;
	}
}
