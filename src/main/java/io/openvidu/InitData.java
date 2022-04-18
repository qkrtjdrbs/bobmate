package io.openvidu;

import io.openvidu.dto.SignUpDto;
import io.openvidu.mvc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Slf4j
@Component
public class InitData {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final UserService userService;

        public void dbInit(){
            for(int i=1;i<10;i++){
                userService.save(new SignUpDto("data"+i, "1234", "test"+i+"@a.com"));
            }
        }
    }
}
