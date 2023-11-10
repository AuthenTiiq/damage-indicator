package com.zenya.damageindicator.nms.fallback;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private ArmorStand armorStand;
        private final LivingEntity entity;
        private final String text;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;
            this.text = text;
        }

        @Override
        public Hologram spawn(double offset, double speed, long duration) {
            sendCreatePacket();
            sendMetaPacket();
            new HologramRunnable(this, entity, offset, speed, duration).start();
            return this;
        }

        @Override
        public void sendCreatePacket() {
            armorStand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class, this::sendMetaPacket);
        }

        @Override
        public void sendMetaPacket() {
            // Metadata is sent from the spawners callback
        }

        public void sendMetaPacket(ArmorStand as) {
            as.setInvulnerable(true);
            as.setVisible(false);
            as.setMarker(true);
            as.setSmall(true);
            as.setGravity(false);
            as.setCustomName(text);
            as.setCustomNameVisible(true);
        }

        @Override
        public void sendTeleportPacket(Location loc) {
            armorStand.teleport(loc);
        }

        @Override
        public void sendRemovePacket() {
            armorStand.remove();
        }

        @Override
        public void sendPacketToTracked(Object packet) {
            unsupported();
        }

        @Override
        public void sendPacketToWorld(Object packet) {
            unsupported();
        }

        @Override
        public void sendPacket(Object connection, Object packet) {
            unsupported();
        }

        private void unsupported() {
            throw new UnsupportedOperationException("Fallback isn't packet based!");
        }

    }

}
