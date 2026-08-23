package com.nuclearenergy.dao;

import com.nuclearenergy.model.Reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory "database" for reactors. A real system would replace this with
 * JDBC/JPA, but for a simple demo an in-memory store keeps the whole app
 * runnable with zero external setup.
 */
public class ReactorDAO {

    private static final ReactorDAO INSTANCE = new ReactorDAO();

    private final List<Reactor> reactors = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idSequence = new AtomicInteger(0);

    private ReactorDAO() {
        add("Reactor Unit 1", "Diablo Canyon", Reactor.ONLINE, 315.0, 1100.0);
        add("Reactor Unit 2", "Diablo Canyon", Reactor.ONLINE, 322.5, 1085.0);
        add("Reactor Unit 3", "Palo Verde", Reactor.MAINTENANCE, 45.0, 0.0);
    }

    public static ReactorDAO getInstance() {
        return INSTANCE;
    }

    public List<Reactor> getAll() {
        synchronized (reactors) {
            return new ArrayList<>(reactors);
        }
    }

    public Optional<Reactor> findById(int id) {
        synchronized (reactors) {
            return reactors.stream().filter(r -> r.getId() == id).findFirst();
        }
    }

    public Reactor add(String name, String location, String status,
                        double temperatureC, double outputMW) {
        Reactor reactor = new Reactor(idSequence.incrementAndGet(), name, location,
                status, temperatureC, outputMW);
        reactors.add(reactor);
        return reactor;
    }

    public boolean delete(int id) {
        synchronized (reactors) {
            return reactors.removeIf(r -> r.getId() == id);
        }
    }

    /** Cycles the reactor's status and returns the updated reactor, if found. */
    public Optional<Reactor> toggleStatus(int id) {
        Optional<Reactor> found = findById(id);
        found.ifPresent(r -> r.setStatus(r.nextStatus()));
        return found;
    }

    public double totalOutputMW() {
        synchronized (reactors) {
            return reactors.stream().mapToDouble(Reactor::getOutputMW).sum();
        }
    }

    public long onlineCount() {
        synchronized (reactors) {
            return reactors.stream().filter(r -> Reactor.ONLINE.equals(r.getStatus())).count();
        }
    }

    /**
     * Nudges temperature/output slightly for ONLINE reactors so the dashboard
     * looks "live" when polled, simulating real sensor jitter.
     */
    public void simulateReadings() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        synchronized (reactors) {
            for (Reactor r : reactors) {
                if (Reactor.ONLINE.equals(r.getStatus())) {
                    double tempDelta = random.nextDouble(-4.0, 6.0);
                    double outputDelta = random.nextDouble(-15.0, 15.0);
                    r.setTemperatureC(Math.max(0, r.getTemperatureC() + tempDelta));
                    r.setOutputMW(Math.max(0, r.getOutputMW() + outputDelta));
                } else {
                    // Idle reactors cool down / stop producing power.
                    r.setTemperatureC(Math.max(20.0, r.getTemperatureC() - 5.0));
                    r.setOutputMW(0.0);
                }
            }
        }
    }
}
