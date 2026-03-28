package com.zzyl.nursing.stategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.domain.Bed;
import com.zzyl.nursing.domain.Floor;
import com.zzyl.nursing.domain.Room;
import com.zzyl.nursing.enums.QueryStrategyEnum;
import com.zzyl.nursing.mapper.BedMapper;
import com.zzyl.nursing.mapper.FloorMapper;
import com.zzyl.nursing.mapper.RoomMapper;
import com.zzyl.nursing.vo.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("memoryStrategy")
public class MemoryBedTreeStrategy implements BedTreeStrategy {
    @Autowired
    private BedMapper bedMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private FloorMapper floorMapper;

    @Override
    public List<TreeVo> getTree(Integer status) {
        List<Bed> beds = bedMapper.selectList(new LambdaQueryWrapper<Bed>().eq(Bed::getBedStatus, 0));
        Map<Long, List<Bed>> roomId2Beds = beds.stream().collect(Collectors.groupingBy(Bed::getRoomId));
        Set<Long> roomIds = beds.stream().map(Bed::getRoomId).collect(Collectors.toSet());

        List<Room> rooms = roomMapper.selectList(new LambdaQueryWrapper<Room>().in(Room::getId, roomIds));
        Map<Long, List<Room>> floorId2Rooms = rooms.stream().collect(Collectors.groupingBy(Room::getFloorId));
        Set<Long> floorIds = rooms.stream().map(Room::getFloorId).collect(Collectors.toSet());

        List<Floor> floors = floorMapper.selectList(new LambdaQueryWrapper<Floor>().in(Floor::getId, floorIds));

        List<TreeVo> vos = new ArrayList<>(floors.size());

        for (Floor floor : floors) {
            TreeVo vo = new TreeVo();
            vos.add(vo);
            vo.setValue(floor.getId().toString());
            vo.setLabel(floor.getName());

            List<Room> roomList = floorId2Rooms.get(floor.getId());
            List<TreeVo> vo1s = new ArrayList<>(roomList.size());
            vo.setChildren(vo1s);

            for (Room room : roomList) {
                TreeVo vo1 = new TreeVo();
                vo1s.add(vo1);
                vo1.setValue(room.getId().toString());
                vo1.setLabel(room.getCode());
                List<Bed> bedList = roomId2Beds.get(room.getId());
                List<TreeVo> vo2s = new ArrayList<>(bedList.size());
                vo1.setChildren(vo2s);
                for (Bed bed : bedList) {
                    TreeVo vo2 = new TreeVo();
                    vo2s.add(vo2);
                    vo2.setValue(bed.getId().toString());
                    vo2.setLabel(bed.getBedNumber());
                    vo2.setChildren(null);
                }

            }
        }

        return vos;
    }

    @Override
    public QueryStrategyEnum getStrategyType() {
        return QueryStrategyEnum.MEMORY;
    }
}