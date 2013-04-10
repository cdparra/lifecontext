/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

/**
 *
 * @author Nicola.Parrello
 */
public class Participant {

    private int participant_id;
    private Life_Event life_event;
    private boolean focus;
    private String status;
    private Person person;
    private Person contributor;

    public Participant() {
    }

    public int getParticipant_id() {
        return participant_id;
    }

    public void setParticipant_id(int participant_id) {
        this.participant_id = participant_id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Life_Event getLife_event() {
        return life_event;
    }

    public void setLife_event(Life_Event life_event) {
        this.life_event = life_event;
    }

    public Person getContributor() {
        return contributor;
    }

    public void setContributor(Person contributor) {
        this.contributor = contributor;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
