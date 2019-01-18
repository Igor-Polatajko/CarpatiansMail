package letterLogic;

import client.core.BaseGmailClient;
import client.core.GmailClient;
import employee.Employee;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;



@SuppressWarnings("serial")
public class Letter implements Serializable{
	private static transient BaseGmailClient client;
	private static transient LetterFormatter letterFormatter;
	
	private static String serverName;

	
	private ArrayList<Employee> employees;
	private String content;
	private String senderEmail;
	private static String bossEmail;
	private static String bossName;
	private int currentLevel;
	private int[] letterState;
	
	private LetterState currentGeneralLetterState = LetterState.UNDEFINED;
	private LocalDateTime[] sendTime;
	
	private String letterID;
	
	
	public Letter(ArrayList<Employee> employees, String senderEmail,String content) {
		
		this.employees = employees;
		this.senderEmail = senderEmail;

		this.content = content;
		this.currentLevel = getMaxLevel();
		this.letterState = new int[employees.size()];
		this.sendTime = new LocalDateTime[employees.size() + 1];
		letterID = UUID.randomUUID().toString();

		sent();
	}
	
	public Letter(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	
	public Letter() {}
	
	
	public static void staticInitialization(String serverNameL, String bossEmailL, String bossNameL, GmailClient clientL) {
		serverName = serverNameL;
		bossEmail = bossEmailL;
		bossName = bossNameL;
		client = clientL;
		letterFormatter = new LetterFormatter(serverName);
	}

	public void setAnswer(boolean isAccepted, String eMail) {
		if(currentGeneralLetterState == LetterState.ACCEPTED) {
			handleBossAnswer(isAccepted);
		}
		else {
			int index = getIndex(eMail);
			if(index == -1) {
				throw new IllegalArgumentException();
			}
			letterState[index] = (isAccepted) ? 1 : -1; 
			if(isCurrentLevelEmpty() || checkLevelAnswers()) {
				levelUp();
			}
		}
	}
	
	public void badAttachmentFormat() {
		client.send(letterFormatter.sentErrorMessage("������� � Excel �������. ǳ������ (*) ���� ������� ���� ��� ����� ���� � ������� �����"
						  + " ��������� ��� ���� ���� � ��� �����", senderEmail));
	}
	
	public void sentBadIDError() {
		client.send(letterFormatter.sentErrorMessage("����� � �������� ID �� ��������!\r\n"
								   + "�������� ����������� ��������� � ���� ID. ", senderEmail));
	}
	
	public void sentBadLetterTypeError() {
		client.send(letterFormatter.sentErrorMessage("���� �� ������� �������, ��������� ��������� ���� ���\r\n"
							       + "����� ����� ������� ���� ���� � ��������� ���: �����, ³������.\r\n"
							       + "����-����� ������� ���� Excel �������.\r\n����-������� �� ������� "
							       + "������ ����������", senderEmail));
	}
	
	public void sentBadAnswerLetterTypeError() {
		client.send(letterFormatter.sentErrorMessage("����-������� �� ������� �������, ��������� ��������� ��� ������\r\n"
			       + "����-������� ������� ��������� ������ � ��������� �������:\r\n"
			       + "ID ���������\r\n"
			       + "ID ³�������", senderEmail));
	}
	
	public void sentAlreadyAcceptedError() {
		client.send(letterFormatter.sentErrorMessage("����� ����� ��� ���� ��������� ��������", senderEmail));
	}
	
	public String getLetterID() {
		return letterID;
	}
	
	public LetterState getCurrentGeneralLetterState() {
		return currentGeneralLetterState;
	}
	
	public LocalDateTime[] getLetterSendTime() {
		return sendTime;
	}
	
	public int[] getLetterState() {
		return letterState;
	}
	
	private void handleBossAnswer(boolean isAccepted) {
		if(isAccepted) {
			sentBackToSenderPositiveAnswer();
			currentGeneralLetterState = LetterState.ACCEPTED_BY_BOSS;
		}
		else {
			ArrayList<Employee> sigleListForBoss = new ArrayList<>();
			sigleListForBoss.add(new Employee(bossName, "�������", bossEmail, -9999));
			sentBackToSenderNegativeAnswer(sigleListForBoss, employees);
			currentGeneralLetterState = LetterState.REJECTED;
		}
	}
	
	
	private int getMaxLevel() {
		int maxLevel = 0;
		for(Employee e : employees) {
			if(e.getLevel() > maxLevel) {
				maxLevel = e.getLevel();
			}
		}
		return maxLevel;
	}
	
	private int getMinLevel() {
		int minLevel = getMaxLevel();
		for(Employee e : employees) {
			if(e.getLevel() < minLevel) {
				minLevel = e.getLevel();
			}
		}
		return minLevel;
	}
	
	
	private int getIndex(String eMail) {
		int index = -1;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getEmail().equals(eMail.trim())) {
				index = i;
			}
		}
		return index;
	}
	
	private boolean checkFullLevel() {
		boolean isAccepted = true;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel && letterState[i] == -1) {
				isAccepted = false;
			}
		}
		return isAccepted;
	}
	
	private boolean checkLevelAnswers() {
		boolean answered = true;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel && letterState[i] == 0) {
				answered = false;
			}
		}
		return answered;
	}
	
	private void levelUp() {

		loop:
		while(true) {
			if(!isCurrentLevelEmpty() && checkLevelAnswers()) {
				if(!checkFullLevel()) {
					currentGeneralLetterState = LetterState.REJECTED;
					sent();
					break loop;
				}
				else if(currentLevel == getMinLevel()) {
					currentGeneralLetterState = LetterState.ACCEPTED;
					sent();
					break loop;
				}
				else {
					currentLevel--;
				}
			}
			
			while(isCurrentLevelEmpty()) {
				if(currentLevel == 0) {
					currentGeneralLetterState = LetterState.ACCEPTED;
					sent();
					break loop;
				}
				currentLevel--;
			}
			sent();
			break;
		}
		
		
	}
	
	private boolean isCurrentLevelEmpty() {
		boolean isEmpty = true;
		for(Employee e : employees) {
			if(e.getLevel() == currentLevel) {
				isEmpty = false;
			}
		}
		return isEmpty;
	}
	
	public void sentToBoss() {
		client.send(letterFormatter.messageToBoss(getWhoAcceptIt(), bossEmail, senderEmail, content,letterID));
		sendTime[sendTime.length - 1] = LocalDateTime.now();
	}
	
	public void sentToPerson(int index) {
		client.send(letterFormatter.messageTo(employees.get(index).getEmail(), senderEmail, content, letterID));
		sendTime[index] = LocalDateTime.now();
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				sentToPerson(i);
			}
		}
	}

	private void sentBackToSenderPositiveAnswer() {
		client.send(letterFormatter.messagePositiveAnswerToSender(senderEmail, content));
	}
	
	private void sentBackToSenderNegativeAnswer(ArrayList<Employee> peopleWhoRejectIt, ArrayList<Employee> peopleWhoAcceptIt) {
		client.send(letterFormatter.messageNegativeAnswerToSender(peopleWhoRejectIt, peopleWhoAcceptIt, senderEmail, content));
	}
	
	private ArrayList<Employee> getWhoRejectIt() {
		ArrayList<Employee> peopleWhoRejectIt = new ArrayList<>();
		for(int i = 0; i < employees.size(); i++) {
			if(letterState[i] == -1) {
				peopleWhoRejectIt.add(employees.get(i));
			}
		}
		return peopleWhoRejectIt;
	}
	
	private ArrayList<Employee> getWhoAcceptIt() {
		ArrayList<Employee> peopleWhoAcceptIt = new ArrayList<>();
		for(int i = 0; i < employees.size(); i++) {
			if(letterState[i] == 1) {
				peopleWhoAcceptIt.add(employees.get(i));
			}
		}
		return peopleWhoAcceptIt;
	}
	
	private void sent() {
		switch(currentGeneralLetterState) {
			case UNDEFINED:
				sentToAllFromCurrentLevel();
				break;
			case ACCEPTED:
				sentToBoss();
				break;
			case REJECTED:
				ArrayList<Employee> peopleWhoRejectIt = getWhoRejectIt();
				ArrayList<Employee> peopleWhoAcceptIt = getWhoAcceptIt();
				sentBackToSenderNegativeAnswer(peopleWhoRejectIt, peopleWhoAcceptIt);
				break;
			case ACCEPTED_BY_BOSS:
				sentAlreadyAcceptedError();
				break;
		}
	}
}
