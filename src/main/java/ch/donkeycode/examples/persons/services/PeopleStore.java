package ch.donkeycode.examples.persons.services;

import ch.donkeycode.examples.persons.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Service
public class PeopleStore {
    private final AtomicReference<List<Person>> personsRef = new AtomicReference<>(new ArrayList<>());

    @PostConstruct
    public void init() {
        addPerson(Person.builder().name("Hans").prename("Muster").build());
        addPerson(Person.builder().name("Anna").prename("Ananas").build());
        addPerson(Person.builder().name("Axel").prename("Axt").build());
    }

    public List<Person> getPersons() {
        return personsRef.get();
    }

    public void update(UUID id, UnaryOperator<Person> updater) {
        personsRef.updateAndGet(people -> {
            people.replaceAll(person -> {
                if (person.getId().equals(id)) {
                    return updater.apply(person);
                }
                return person;
            });

            return people;
        });
    }

    public void addPerson(Person person) {
        personsRef.updateAndGet(persons -> {
            List<Person> newPersons = new ArrayList<>(persons);
            newPersons.add(person);
            return newPersons;
        });
    }
}
