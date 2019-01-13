package parser;

import java.util.ArrayList;

import letterLogic.Letter;

public class LetterTypeChecker {

	public boolean isAnswerPositive(String answerLetterContent) {
		if(answerLetterContent.trim().split(" ")[1].equals("���������")) {
			return true;
		}
		else if(answerLetterContent.trim().split(" ")[1].equals("³�������")) {
			return false;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public Letter getLetterById(String id, ArrayList<Letter> letters) {
		for(Letter l : letters) {
			if(l.getLetterID().equals(id.trim())) {
				return l;
			}
		}
		throw new IllegalArgumentException();
	}
}
