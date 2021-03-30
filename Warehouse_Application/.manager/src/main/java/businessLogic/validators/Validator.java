package businessLogic.validators;

/**
 * Interface that is used as a black box for implementing
 * various validators for the models.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 * @param <T> : the object for which the validator
 * will apply
 */
public interface Validator<T> {

	public void validate(T object);
}
