package com.BGK.reggie.service.impl;

import com.BGK.reggie.mapper.AddressBookMapper;
import com.BGK.reggie.pojo.AddressBook;
import com.BGK.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
