package com.kh.auction.service;

import com.kh.auction.domain.Replies;
import com.kh.auction.repo.RepliesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepliesService {

    @Autowired
    private RepliesDAO repliesDAO;

    public List<Replies> showAll() {
        return repliesDAO.findAll();
    }

    public Replies show(int id) {
        return repliesDAO.findById(id).orElse(null);
    }

    public Replies create(Replies replies) {
        return repliesDAO.save(replies);
    }

    public Replies update(Replies replies) {
        Replies target = repliesDAO.findById(replies.getReplyNo()).orElse(null);
        if (target!=null) {
            return repliesDAO.save(replies);
        }
        return null;
    }

    public Replies delete(int id) {
        Replies data = repliesDAO.findById(id).orElse(null);
        repliesDAO.delete(data);
        return data;
    }

}
