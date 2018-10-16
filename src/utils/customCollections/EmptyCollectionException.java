package utils.customCollections;

@SuppressWarnings("serial")
public class EmptyCollectionException extends RuntimeException {
	public EmptyCollectionException () {
		super("This collection is empty");
	}
}
