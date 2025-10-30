package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.model.OutboxEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends GenericRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(EventStatus status);
}
