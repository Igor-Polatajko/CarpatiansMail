package parser;

import java.util.ArrayList;

import letterLogic.Letter;
import letterLogic.LetterType;

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
	
	public String getLetterID(String content) {
		return content.trim().split(" ")[0];
	}
	
	public LetterType IsRequest(String subject, boolean isAttachment) {
		if(subject.trim().equals("�����") && isAttachment) {
			return LetterType.REQUEST;
		}
		else if(subject.trim().equals("³������") && !isAttachment) {
			return LetterType.ANSWER;
		}
		else {
			return LetterType.UNDEFINED;
		}
	}	
}
