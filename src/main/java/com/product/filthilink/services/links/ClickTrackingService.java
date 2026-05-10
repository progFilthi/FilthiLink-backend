package com.product.filthilink.services.links;

import com.product.filthilink.repositories.links.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClickTrackingService {

    private final LinkRepository linkRepository;

    @Async
    @Transactional
    public void increment( Long linkId ) {
        linkRepository.findById(linkId).ifPresent(link -> {
            link.setClickCount(link.getClickCount() + 1);
            linkRepository.save(link);
        });
    }


}
