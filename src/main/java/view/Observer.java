package view;

import controller.Subject;
/**
 * @Author Martin Stemmer
 *
 */
public interface Observer {

    void update(Subject sub);
}