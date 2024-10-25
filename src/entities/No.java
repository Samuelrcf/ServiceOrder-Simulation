package entities;

import java.io.Serializable;

public class No implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private char character;
	private int frequency;
	private No left, right;

	public No(char character, int frequency) {
		this.character = character;
		this.frequency = frequency;
		left = null;
		right = null;
	}

	public char getCharacter() {
		return character;
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public No getLeft() {
		return left;
	}

	public void setLeft(No left) {
		this.left = left;
	}

	public No getRight() {
		return right;
	}

	public void setRight(No right) {
		this.right = right;
	}

}