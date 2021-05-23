import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './food.reducer';
import { IFood } from 'app/shared/model/food.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFoodUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FoodUpdate = (props: IFoodUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { foodEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/food');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...foodEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="baigkApp.food.home.createOrEditLabel" data-cy="FoodCreateUpdateHeading">
            <Translate contentKey="baigkApp.food.home.createOrEditLabel">Create or edit a Food</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : foodEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="food-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="food-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="food-name">
                  <Translate contentKey="baigkApp.food.name">Name</Translate>
                </Label>
                <AvField id="food-name" data-cy="name" type="text" name="name" />
              </AvGroup>
              <AvGroup>
                <Label id="catoryLabel" for="food-catory">
                  <Translate contentKey="baigkApp.food.catory">Catory</Translate>
                </Label>
                <AvField id="food-catory" data-cy="catory" type="text" name="catory" />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="food-description">
                  <Translate contentKey="baigkApp.food.description">Description</Translate>
                </Label>
                <AvField id="food-description" data-cy="description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label id="caloLabel" for="food-calo">
                  <Translate contentKey="baigkApp.food.calo">Calo</Translate>
                </Label>
                <AvField id="food-calo" data-cy="calo" type="text" name="calo" />
              </AvGroup>
              <AvGroup>
                <Label id="priceLabel" for="food-price">
                  <Translate contentKey="baigkApp.food.price">Price</Translate>
                </Label>
                <AvField id="food-price" data-cy="price" type="text" name="price" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/food" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  foodEntity: storeState.food.entity,
  loading: storeState.food.loading,
  updating: storeState.food.updating,
  updateSuccess: storeState.food.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FoodUpdate);
