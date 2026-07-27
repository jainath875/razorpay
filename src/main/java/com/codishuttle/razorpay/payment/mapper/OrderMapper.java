package com.codishuttle.razorpay.payment.mapper;

import com.codishuttle.razorpay.payment.dto.response.OrderResponse;
import com.codishuttle.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);

}
