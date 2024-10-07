package com.benection.babymoment.api.config;

import com.benection.babymoment.api.entity.Account;
import com.benection.babymoment.api.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Account> optionalAccount = accountRepository.findByAccountIdAndIsDeletedFalse(Long.parseLong(username));
        if (optionalAccount.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return createUserDetails(optionalAccount.get());
    }

    /**
     * db에 account 값이 존재한다면 UserDetails 객체로 만들어서 반환한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    private UserDetails createUserDetails(Account account) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getAuthority());

        return new User(Long.toString(account.getAccountId()), account.getPassword(), Collections.singleton(grantedAuthority));
    }
}
