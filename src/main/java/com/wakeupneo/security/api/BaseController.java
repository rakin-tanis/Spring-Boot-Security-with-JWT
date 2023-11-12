package com.wakeupneo.security.api;

import com.wakeupneo.security.dto.RoleResponse;
import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import com.wakeupneo.security.model.User;
import com.wakeupneo.security.model.UserPrincipal;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class BaseController {

    public static final String BASE_PATH = "/api/v1/";
    public static final String JSON = APPLICATION_JSON_VALUE;
    public static final String TOKEN_HEADER = "authorization";


    final ModelMapper mapper;

    public BaseController(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUser();
    }

    public <S, D> D map(S sourceInstance, Class<D> destinationTypeClass) {
        if (!ObjectUtils.isEmpty(mapper) && !ObjectUtils.isEmpty(mapper.getConfiguration())) {
            mapper.getConfiguration().setAmbiguityIgnored(true);
        }
        return mapper.map(sourceInstance, destinationTypeClass);
    }

    public <S, D> D map(S sourceInstance, Class<D> destinationTypeClass, String typeMapName) {
        if (!ObjectUtils.isEmpty(mapper) && !ObjectUtils.isEmpty(mapper.getConfiguration())) {
            mapper.getConfiguration().setAmbiguityIgnored(true);
        }
        return mapper.map(sourceInstance, destinationTypeClass, typeMapName);
    }

    public <S, D> D map(S sourceInstance, Type destinationTypeClass) {
        if (!ObjectUtils.isEmpty(mapper) && !ObjectUtils.isEmpty(mapper.getConfiguration())) {
            mapper.getConfiguration().setAmbiguityIgnored(true);
        }
        return mapper.map(sourceInstance, destinationTypeClass);
    }

    public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        if (!ObjectUtils.isEmpty(mapper) && !ObjectUtils.isEmpty(mapper.getConfiguration())) {
            mapper.getConfiguration().setAmbiguityIgnored(true);
        }
        return entityList.stream().map(entity -> map(entity, outCLass)).collect(Collectors.toList());
    }
}
