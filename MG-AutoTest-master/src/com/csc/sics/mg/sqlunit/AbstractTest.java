package com.csc.sics.mg.sqlunit;

public abstract class AbstractTest {

	private double numericTolerance;

	public AbstractTest setNumericTolerance(final double aTolerance) {
		numericTolerance = aTolerance;
		return this;
	}

	String checkEqual(final String aName, final Object c1, final Object c2) {
		if (c1 == null && c2 == null) {
			return null;
		}
		if (c1 == null && c2 != null) {
			return this.failCheckEquals(aName, c1, c2);
		} else if (c1 != null && c2 == null) {
			return this.failCheckEquals(aName, c1, c2);
		} else if (c1 instanceof Number && c2 instanceof Number) {
			if (SqlTestUtils.compare((Number) c1, (Number) c2, numericTolerance) != 0) {
				return this.failCheckEquals(aName, c1, c2);
			}
		} else if (!c1.equals(c2)) {
			return this.failCheckEquals(aName, c1, c2);
		}
		return null;
	}

	abstract String failCheckEquals(String aName, Object c1, Object c2);

}
