package letterLogic;

import java.util.ArrayList;

import client.core.common.SendedMessage;
import employee.Employee;

public class LetterFormatter {
	
	String serverName;
	
	public LetterFormatter(String serverName) {
		this.serverName = serverName;
	}
	

	public SendedMessage messageTo(String eMail, String senderEmail, String content, String letterID) {
		return new SendedMessage("�����", letterID +" - �������� �� � ������� ������ ������ � ������ \r\n"+
								"����� �� " + senderEmail + "\r\n\r\n" + content)
				.from(serverName)
				.to(eMail.trim());
	}
	
	public SendedMessage messageToBoss(ArrayList<Employee> peopleWhoAcceptIt, String eMail, String senderEmail, String content, String letterID) {
		
		StringBuffer whoAcceptItString = new StringBuffer();
		
		for(Employee e : peopleWhoAcceptIt) {
			whoAcceptItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
		}
		
		return new SendedMessage("�����", letterID +" - �������� �� � ������� ������ ������ � ������ \r\n"+
								"����� �� " + senderEmail + "\r\n\r\n"+
								"����� ��������: \r\n" +( peopleWhoAcceptIt.size()>0 ? whoAcceptItString :
																						"(������ ������)")		
														+ "\r\n\r\n"
														+ content)
				.from(serverName)
				.to(eMail.trim());
	}

	public SendedMessage messagePositiveAnswerToSender(String senderEmail, String content) {
		return new SendedMessage("³������","��� ����� ���������!!!\r\n\r\n"+ content)
				.from(serverName)
				.to(senderEmail);
	}
	
	public SendedMessage messageNegativeAnswerToSender(ArrayList<Employee> peopleWhoRejectIt, ArrayList<Employee> peopleWhoAcceptIt,  String senderEmail, String content) {
		
		StringBuffer whoRejectItString = new StringBuffer();
		StringBuffer whoAcceptItString = new StringBuffer();
		
		
		for(Employee e : peopleWhoRejectIt) {
			whoRejectItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
		}
		
		for(Employee e : peopleWhoAcceptIt) {
			whoAcceptItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
		}
		
		return new SendedMessage("³������","��� ����� ��������!\r\n\r\n"
				+ ((peopleWhoRejectIt.size() > 0) ? "����� ��������: \r\n"+ whoRejectItString  + "\r\n\r\n" : "")
				+ ((peopleWhoAcceptIt.size() > 0) ? "����� ��������: \r\n" + whoAcceptItString  + "\r\n\r\n" : "")
				+ "�������� ������ ���� �� ��������� �����, ���� �� ����� ���� ���� � ���� ������\r\n\r\n"
				+ content)
				.from(serverName)
				.to(senderEmail);
	}
	
	public SendedMessage sentErrorMessage(String errMessage,  String senderEmail) {
		return new SendedMessage( "������� �����������!!!","���� ������� �� ����� " + senderEmail
				+ " �� ���� ����������\r\n"+ errMessage)
			.from(serverName)
			.to(senderEmail);
	}
	
}
