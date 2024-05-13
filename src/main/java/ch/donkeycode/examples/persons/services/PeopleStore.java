package ch.donkeycode.examples.persons.services;

import ch.donkeycode.examples.persons.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PeopleStore {
    private final AtomicReference<List<Person>> personsRef = new AtomicReference<>(new ArrayList<>());

    public List<Person> getPersons() {
        return personsRef.get();
    }

    public void addPerson(Person person) {
        personsRef.updateAndGet(persons -> {
            List<Person> newPersons = new ArrayList<>(persons);
            newPersons.add(person);
            return newPersons;
        });
    }
}
