package cn.eakay.commons.util.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 
 * 
 * pair排序类，first second 传入实现Comparable接口的类
 * @author hymagic
 * @param <A>
 * @param <B>
 */
public class Pair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<Pair<A, B>> {
	public A first;
	public B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public int compareTo(Pair<A, B> otherPair) {
		A otherFirst = otherPair.first;
		int resFirst = first.compareTo(otherFirst);
		if (resFirst != 0) {
			return resFirst;
		}
		return second.compareTo(otherPair.second);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair<?, ?>)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		return isEqual(first,  ((Pair<?, ?>)obj).first ) &&
			   isEqual(second, ((Pair<?, ?>)obj).second);
	}
	
	private boolean isEqual(Object o1, Object o2) {
		return o1 == null ? o2 == null : (o1 == o2 || o1.equals(o2));
	}

	public int hashCode() {
		if (first == null) {
			return (second == null) ? 0 : second.hashCode() + 1;
		} else if (second == null) {
			return first.hashCode() + 2;
		} else {
			return first.hashCode() * 17 + second.hashCode();
		}
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ')';
	}
	
	
}