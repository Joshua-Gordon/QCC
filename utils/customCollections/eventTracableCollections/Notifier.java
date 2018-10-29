package utils.customCollections.eventTracableCollections;

import java.io.Serializable;

public class Notifier implements Serializable {
	private static final long serialVersionUID = -4355135792535307514L;
	
	private static final Notifier NO_NOTIFIER;
	private static final ReceivedEvent NO_EVENT;
	
	static {
		NO_EVENT = (f1, f2, f3) -> {};
		
		NO_NOTIFIER = new Notifier() {
			private static final long serialVersionUID = -7938646971850743846L;
			@Override
			public void sendChange(Object source, String methodName, Object... args) {}
			@Override
			public void setReceiver(Notifier receiver) {}
		};
		
		NO_NOTIFIER.receiver = null;
	}
	
	private Notifier receiver;
	private transient ReceivedEvent receivedEvent;
	
	public Notifier() {
		this(NO_NOTIFIER);
	}
	
	public Notifier(Notifier receiver) {
		this(receiver, NO_EVENT);
	}
	
	public Notifier(Notifier receiver, ReceivedEvent receivedEvent) {
		setReceiver(receiver);
		setReceivedEvent(NO_EVENT);
	}
	
	public void setReceiver(Notifier receiver) {
		this.receiver = receiver == null ? NO_NOTIFIER : receiver;
	}
	
	public void setReceivedEvent(ReceivedEvent receivedEvent) {
		this.receivedEvent = receivedEvent == null ? NO_EVENT : receivedEvent;
	}
	
	public void sendChange(Object source, String methodName, Object... args) {
		receiver.retrieve(source, methodName, args);
	}
	
	private void retrieve(Object source, String methodName, Object... args) {
		receivedEvent.receive(source, methodName, args);
		sendChange(source, methodName, args);
	}
	
	public static interface ReceivedEvent {
		public void receive(Object source, String methodName, Object ... args);
	}
	
}
