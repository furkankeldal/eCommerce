package com.eticaret.stock.service;

import com.eticaret.stock.model.Sequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
@RequiredArgsConstructor
public class SequenceService {
    
    private final MongoOperations mongoOperations;
    
    public Long getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Sequence counter = mongoOperations.findAndModify(
            query,
            update,
            options().returnNew(true).upsert(true),
            Sequence.class
        );
        return counter != null ? counter.getSeq() : 1L;
    }
}

